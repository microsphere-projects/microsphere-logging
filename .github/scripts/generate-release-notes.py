#!/usr/bin/env python3
"""
Release Notes Generator

Generates release notes between two git tags using GitHub Models API (free tier).

Features:
- Fetches commits between specified tags
- Categorizes commits by conventional commit type (feat, fix, chore, docs, etc.)
- Uses GitHub Models API with GPT-4o to generate intelligent release notes
- Formats output as Markdown
- Supports appending to existing release-notes.md file

Usage:
    python3 generate-release-notes.py --from <previous-tag> --to <current-tag> [--output release-notes.md]

Environment Variables:
    GH_TOKEN: GitHub token for API authentication (required for AI generation)
"""

import os
import sys
import json
import argparse
import subprocess
import urllib.request
import urllib.error
from datetime import datetime
from typing import Optional, List, Dict, Tuple


class GitCommitParser:
    """Parses git commits and categorizes them."""
    
    CATEGORY_PATTERNS = {
        'New Features': r'^feat(\(.+\))?:',
        'Bug Fixes': r'^fix(\(.+\))?:',
        'Documentation': r'^docs(\(.+\))?:',
        'Dependency Updates': r'^(chore|deps)(\(.+\))?:.*(?:bump|update|upgrade)',
        'Performance': r'^perf(\(.+\))?:',
        'Refactoring': r'^refactor(\(.+\))?:',
        'Test Improvements': r'^test(\(.+\))?:',
        'Build and Workflow Enhancements': r'^(chore|ci)(\(.+\))?:.*(?:workflow|build|ci)',
        'Other Changes': r'^chore(\(.+\))?:',
    }
    
    def __init__(self, repo_path: str = '.'):
        """Initialize the parser with a repository path."""
        self.repo_path = repo_path
    
    def get_commits_between_tags(self, from_tag: str, to_tag: str) -> List[Dict[str, str]]:
        """
        Fetch commits between two git tags.
        
        Args:
            from_tag: Starting tag (exclusive)
            to_tag: Ending tag (inclusive)
            
        Returns:
            List of commit dictionaries with 'hash', 'message', and 'author' keys
        """
        try:
            # Get commits in format: hash|message|author
            cmd = [
                'git',
                '-C', self.repo_path,
                'log',
                '--oneline',
                '--pretty=format:%h|%s|%an',
                f'{from_tag}..{to_tag}'
            ]
            
            result = subprocess.run(cmd, capture_output=True, text=True, check=True)
            
            commits = []
            for line in result.stdout.strip().split('\n'):
                if not line.strip():
                    continue
                parts = line.split('|', 2)
                if len(parts) >= 2:
                    commits.append({
                        'hash': parts[0],
                        'message': parts[1],
                        'author': parts[2] if len(parts) > 2 else 'Unknown'
                    })
            
            return commits
        except subprocess.CalledProcessError as e:
            print(f"Error fetching commits: {e.stderr}", file=sys.stderr)
            return []
    
    def categorize_commits(self, commits: List[Dict[str, str]]) -> Dict[str, List[Dict[str, str]]]:
        """
        Categorize commits by type based on conventional commit patterns.
        
        Args:
            commits: List of commit dictionaries
            
        Returns:
            Dictionary mapping category names to lists of commits
        """
        import re
        
        categorized = {category: [] for category in self.CATEGORY_PATTERNS.keys()}
        uncategorized = []
        
        for commit in commits:
            message = commit['message']
            categorized_flag = False
            
            # Try to match patterns in order of priority
            for category, pattern in self.CATEGORY_PATTERNS.items():
                if re.match(pattern, message, re.IGNORECASE):
                    categorized[category].append(commit)
                    categorized_flag = True
                    break
            
            if not categorized_flag:
                uncategorized.append(commit)
        
        # Add uncategorized commits to "Other Changes" if any
        if uncategorized:
            categorized['Other Changes'].extend(uncategorized)
        
        # Remove empty categories
        return {k: v for k, v in categorized.items() if v}
    
    def format_commits_for_prompt(self, commits: List[Dict[str, str]]) -> str:
        """
        Format commits into a readable string for the AI prompt.
        
        Args:
            commits: List of commit dictionaries
            
        Returns:
            Formatted string of commits
        """
        lines = []
        for commit in commits:
            lines.append(f"- {commit['message']} ({commit['hash']})")
        return '\n'.join(lines)


class GitHubModelsClient:
    """Client for GitHub Models API."""
    
    DEFAULT_MODEL = 'gpt-4o'
    DEFAULT_ENDPOINT = 'https://models.inference.ai.azure.com/chat/completions'
    
    def __init__(self, token: Optional[str] = None, model: str = DEFAULT_MODEL):
        """
        Initialize the GitHub Models client.
        
        Args:
            token: GitHub token for authentication (defaults to GH_TOKEN env var)
            model: Model to use (default: gpt-4o)
        """
        self.token = token or os.environ.get('GH_TOKEN')
        self.model = model
        
        if not self.token:
            raise ValueError(
                'GitHub token is required. Set GH_TOKEN environment variable or pass token parameter.'
            )
    
    def generate_release_notes(self, commits_by_category: Dict[str, List[Dict[str, str]]], 
                              version: str, previous_version: str) -> str:
        """
        Generate release notes using GitHub Models API.
        
        Args:
            commits_by_category: Dictionary of categorized commits
            version: Current version
            previous_version: Previous version for comparison
            
        Returns:
            Generated release notes as markdown string
        """
        # Format commits for the prompt
        formatted_commits = {}
        for category, commits in commits_by_category.items():
            formatted_commits[category] = self._format_commits_for_ai(commits)
        
        # Create the prompt
        prompt = self._create_prompt(formatted_commits, version, previous_version)
        
        # Call the API
        return self._call_api(prompt)
    
    def _format_commits_for_ai(self, commits: List[Dict[str, str]]) -> str:
        """Format commits for AI consumption."""
        lines = []
        for commit in commits:
            lines.append(f"  - {commit['message']} ({commit['hash']})")
        return '\n'.join(lines)
    
    def _create_prompt(self, formatted_commits: Dict[str, str], 
                       version: str, previous_version: str) -> str:
        """Create the prompt for the AI model."""
        prompt = f"""Generate professional release notes for version {version}.

Previous version: {previous_version}

Commits organized by category:
"""
        
        for category, commits_text in formatted_commits.items():
            if commits_text.strip():
                prompt += f"\n{category}:\n{commits_text}"
        
        prompt += f"""

Please format the output as markdown with the following guidelines:
1. Use clear section headers for each category
2. Include commit hashes in parentheses
3. Be concise but informative
4. Group related changes together
5. Highlight breaking changes if any
6. For dependency updates, mention the updated version
7. Keep it professional and suitable for release notes

Do NOT include a "Full Changelog" section or URL comparisons - we'll add those separately.
"""
        return prompt
    
    def _call_api(self, prompt: str) -> str:
        """Call the GitHub Models API."""
        try:
            payload = json.dumps({
                "model": self.model,
                "messages": [{"role": "user", "content": prompt}]
            }).encode()
            
            req = urllib.request.Request(
                self.DEFAULT_ENDPOINT,
                data=payload,
                headers={
                    "Content-Type": "application/json",
                    "Authorization": f"token {self.token}"
                }
            )
            
            with urllib.request.urlopen(req) as resp:
                data = json.loads(resp.read())
                return data["choices"][0]["message"]["content"]
                
        except urllib.error.HTTPError as e:
            error_body = e.read().decode() if e.fp else ""
            print(f"API Error ({e.code}): {error_body}", file=sys.stderr)
            raise RuntimeError(f"Failed to generate release notes via GitHub Models API: {e}")
        except Exception as e:
            print(f"Error calling API: {e}", file=sys.stderr)
            raise RuntimeError(f"Failed to generate release notes: {e}")


class ReleaseNotesGenerator:
    """Main release notes generator."""
    
    def __init__(self, repo_path: str = '.'):
        """Initialize the generator."""
        self.repo_path = repo_path
        self.parser = GitCommitParser(repo_path)
    
    def generate(self, from_tag: str, to_tag: str, output_file: Optional[str] = None) -> str:
        """
        Generate release notes between two tags.
        
        Args:
            from_tag: Starting tag
            to_tag: Ending tag
            output_file: Optional file to append notes to
            
        Returns:
            Generated release notes as markdown string
        """
        # Get commits
        print(f"Fetching commits between {from_tag} and {to_tag}...", file=sys.stderr)
        commits = self.parser.get_commits_between_tags(from_tag, to_tag)
        
        if not commits:
            print(f"No commits found between {from_tag} and {to_tag}", file=sys.stderr)
            return f"No changes between {from_tag} and {to_tag}"
        
        print(f"Found {len(commits)} commits", file=sys.stderr)
        
        # Categorize commits
        print("Categorizing commits...", file=sys.stderr)
        categorized = self.parser.categorize_commits(commits)
        
        # Generate release notes using AI
        print("Generating release notes with AI...", file=sys.stderr)
        client = GitHubModelsClient()
        release_notes = client.generate_release_notes(categorized, to_tag, from_tag)
        
        # Format the output
        output = f"## v{to_tag}\n\n{release_notes}\n\n"
        
        # Add comparison link
        repo_url = self._get_repo_url()
        if repo_url:
            output += f"**Full Changelog**: {repo_url}/compare/{from_tag}...{to_tag}\n"
        
        # Save to file if specified
        if output_file:
            self._append_to_file(output_file, output)
            print(f"Release notes appended to {output_file}", file=sys.stderr)
        
        return output
    
    def _get_repo_url(self) -> Optional[str]:
        """Get the repository URL from git config."""
        try:
            result = subprocess.run(
                ['git', '-C', self.repo_path, 'config', '--get', 'remote.origin.url'],
                capture_output=True,
                text=True,
                check=True
            )
            url = result.stdout.strip()
            # Convert SSH URL to HTTPS if needed
            if url.startswith('git@'):
                url = url.replace(':', '/').replace('git@', 'https://')
            # Remove .git suffix
            url = url.rstrip('/')
            if url.endswith('.git'):
                url = url[:-4]
            return url
        except Exception:
            return None
    
    def _append_to_file(self, file_path: str, content: str) -> None:
        """Append content to a file, creating it if it doesn't exist."""
        # Ensure directory exists
        os.makedirs(os.path.dirname(file_path) or '.', exist_ok=True)
        
        # Create header if file doesn't exist
        if not os.path.exists(file_path):
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write("# Release Notes\n\n")
        
        # Append content
        with open(file_path, 'a', encoding='utf-8') as f:
            f.write(content)


def main():
    """Main entry point."""
    parser = argparse.ArgumentParser(
        description='Generate release notes between git tags using GitHub Models API',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Generate notes for v1.0.0 based on previous tag
  python3 generate-release-notes.py --to v1.0.0

  # Generate notes between specific tags
  python3 generate-release-notes.py --from v0.9.0 --to v1.0.0

  # Save to file
  python3 generate-release-notes.py --from v0.9.0 --to v1.0.0 --output release-notes.md

Environment:
  GH_TOKEN: GitHub token for API authentication (required)
        """
    )
    
    parser.add_argument(
        '--from',
        dest='from_tag',
        help='Previous/starting tag (if not provided, finds the most recent previous tag)'
    )
    parser.add_argument(
        '--to',
        dest='to_tag',
        required=True,
        help='Current/ending tag (required)'
    )
    parser.add_argument(
        '--output',
        '-o',
        help='Output file to append release notes to (default: release-notes.md)'
    )
    parser.add_argument(
        '--repo',
        default='.',
        help='Repository path (default: current directory)'
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Print notes without saving to file'
    )
    
    args = parser.parse_args()
    
    try:
        # Find previous tag if not specified
        if not args.from_tag:
            try:
                result = subprocess.run(
                    ['git', '-C', args.repo, 'describe', '--tags', '--abbrev=0', 
                     '--exclude=' + args.to_tag, 'HEAD'],
                    capture_output=True,
                    text=True,
                    check=True
                )
                args.from_tag = result.stdout.strip()
                print(f"Using previous tag: {args.from_tag}", file=sys.stderr)
            except subprocess.CalledProcessError:
                print("Error: Could not find previous tag. Please specify with --from", file=sys.stderr)
                sys.exit(1)
        
        # Generate release notes
        generator = ReleaseNotesGenerator(args.repo)
        output_file = None if args.dry_run else (args.output or 'release-notes.md')
        
        release_notes = generator.generate(args.from_tag, args.to_tag, output_file)
        
        # Print the generated notes
        print("\n" + "="*60)
        print("Generated Release Notes:")
        print("="*60)
        print(release_notes)
        
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == '__main__':
    main()
