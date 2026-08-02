# Release Notes Generator

A Python script to automatically generate release notes between git tags using GitHub Models API with GPT-4o.

## Overview

The `generate-release-notes.py` script:
- Fetches commits between specified git tags
- Categorizes commits by type (features, bug fixes, dependency updates, etc.)
- Uses **GitHub Models API** (free tier with GPT-4o) to intelligently summarize changes
- Generates professional markdown-formatted release notes
- Optionally appends notes to an existing `release-notes.md` file

## Features

✅ **Automated Commit Analysis**
- Groups commits by conventional commit types (feat, fix, chore, docs, perf, test, etc.)
- Automatically detects and categorizes dependency updates
- Handles merges and CI-related commits intelligently

✅ **AI-Powered Summaries**
- Uses GitHub's free Models API with GPT-4o
- Generates clear, professional release notes
- No external dependencies required (uses standard library only)

✅ **Flexible Usage**
- Works with any two git tags
- Auto-detects previous tag if not specified
- Dry-run mode for preview without saving
- Outputs comparison links for GitHub

✅ **Production Ready**
- Error handling for missing tags and API failures
- Detailed logging to stderr
- Can be integrated into CI/CD workflows

## Installation

### Requirements
- Python 3.6+
- Git
- GitHub token with repository access

### Setup

```bash
# No external dependencies - uses Python standard library!
python3 generate-release-notes.py --help
```

## Usage

### Basic Usage

Generate release notes between two tags:

```bash
export GH_TOKEN="your-github-token"
python3 generate-release-notes.py --from v1.0.0 --to v1.1.0
```

### Auto-Detect Previous Tag

If you don't specify `--from`, the script finds the most recent previous tag:

```bash
export GH_TOKEN="your-github-token"
python3 generate-release-notes.py --to v1.1.0
```

### Save to File

Append generated notes to `release-notes.md`:

```bash
export GH_TOKEN="your-github-token"
python3 generate-release-notes.py --from v1.0.0 --to v1.1.0 --output release-notes.md
```

### Dry-Run Mode

Preview the generated notes without saving to a file:

```bash
export GH_TOKEN="your-github-token"
python3 generate-release-notes.py --from v1.0.0 --to v1.1.0 --dry-run
```

### Custom Repository Path

```bash
export GH_TOKEN="your-github-token"
python3 generate-release-notes.py --from v1.0.0 --to v1.1.0 --repo /path/to/repo
```

## Command-Line Options

```
--from TAG          Previous/starting tag (auto-detected if omitted)
--to TAG            Current/ending tag (required)
--output FILE       Output file to append notes to (default: release-notes.md)
--repo PATH         Repository path (default: current directory)
--dry-run           Print notes without saving to file
--help              Show help message and exit
```

## Environment Variables

### Required
- `GH_TOKEN` - GitHub personal access token for API authentication

Generate a token at: https://github.com/settings/tokens

Token needs minimal permissions:
- `public_repo` (for public repositories)
- `repo` (for private repositories)

### Optional
- `GH_MODELS_ENDPOINT` - Custom API endpoint (defaults to Azure GitHub Models)
- `GH_MODELS_MODEL` - Model to use (defaults to `gpt-4o`)

## API Configuration

The script uses **GitHub Models API** powered by Azure:
- **Endpoint**: `https://models.inference.ai.azure.com/chat/completions`
- **Model**: `gpt-4o` (GPT-4 optimized for speed)
- **Free Tier**: Yes - included with GitHub account
- **Rate Limits**: Check GitHub Models documentation

### Switching to Different Models

The script supports any model available via GitHub Models API:

```python
# Modify in the script:
client = GitHubModelsClient(model='gpt-4-turbo')
```

Available models typically include:
- `gpt-4o` (recommended, fast & capable)
- `gpt-4-turbo`
- `claude-3.5-sonnet`
- `llama-3.1-405b`
- `mistral-large`
- `phi-3-medium`

## Output Format

The script generates markdown release notes organized by category:

```markdown
## v1.1.0

### New Features
- Added new logging configuration API ([abc123](#))
- Implemented async log processing ([def456](#))

### Bug Fixes  
- Fixed null pointer in log formatter ([ghi789](#))

### Dependency Updates
- Bumped log4j to 2.20.0 ([jkl012](#))

### Documentation
- Updated API documentation ([mno345](#))

### Other Changes
- Code cleanup and refactoring ([pqr678](#))

**Full Changelog**: https://github.com/org/repo/compare/v1.0.0...v1.1.0
```

## Integration with GitHub Actions

### Example: Workflow for Publishing Releases

```yaml
name: Generate Release Notes

on:
  push:
    tags:
      - 'v*'

jobs:
  release-notes:
    runs-on: ubuntu-latest
    permissions:
      contents: write
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      
      - name: Generate Release Notes
        env:
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: python3 generate-release-notes.py --to ${{ github.ref_name }}
      
      - name: Commit and Push
        run: |
          git config user.name "github-actions[bot]"
          git config user.email "github-actions[bot]@users.noreply.github.com"
          git add release-notes.md
          git commit -m "docs: update release notes for ${{ github.ref_name }}"
          git push
```

## Commit Categorization

The script automatically categorizes commits using conventional commit patterns:

| Category | Pattern |
|----------|---------|
| New Features | `feat:` |
| Bug Fixes | `fix:` |
| Documentation | `docs:` |
| Performance | `perf:` |
| Refactoring | `refactor:` |
| Test Improvements | `test:` |
| Dependency Updates | `chore:` or `deps:` + (bump\|update\|upgrade) |
| Build & Workflow | `chore:` or `ci:` + (workflow\|build\|ci) |
| Other Changes | All others (chore, etc.) |

### Example Commits

```bash
git commit -m "feat(logging): add new config API"           # → New Features
git commit -m "fix(formatter): handle null values"          # → Bug Fixes  
git commit -m "chore(deps): bump log4j to 2.20.0"          # → Dependency Updates
git commit -m "docs: update README"                        # → Documentation
git commit -m "test(utils): add more test coverage"        # → Test Improvements
git commit -m "chore: merge main into release [skip ci]"   # → Other Changes
```

## Troubleshooting

### "GitHub token is required"
```bash
# Solution: Set the GH_TOKEN environment variable
export GH_TOKEN="ghp_your_token_here"
```

### "No commits found between tags"
```bash
# Verify tags exist:
git tag --list

# Verify commits between tags:
git log TAG1..TAG2
```

### "API Error 401"
```bash
# Solution: Check GitHub token validity and permissions
# Regenerate token at: https://github.com/settings/tokens
```

### "API Error 429" (Rate limited)
```bash
# Solution: Wait before running again
# GitHub Models API has rate limits - check your token quota
```

### Script not found
```bash
# Make script executable:
chmod +x generate-release-notes.py

# Run with python3 explicitly:
python3 generate-release-notes.py --to v1.0.0
```

## Implementation Details

### Architecture

```
generate-release-notes.py
├── GitCommitParser          # Fetches and categorizes commits
├── GitHubModelsClient       # Communicates with GitHub Models API
└── ReleaseNotesGenerator    # Orchestrates the pipeline
```

### Key Components

**GitCommitParser**
- Uses `git log` to fetch commits between tags
- Parses commit messages with regex patterns
- Groups commits into semantic categories
- Formats commits for AI processing

**GitHubModelsClient**
- Creates intelligent prompts for the AI model
- Handles API authentication with GitHub token
- Parses JSON responses
- Provides error handling and retry logic

**ReleaseNotesGenerator**
- Orchestrates the full pipeline
- Manages file I/O and formatting
- Handles edge cases (missing tags, no commits, etc.)
- Generates GitHub comparison links

### Security

✅ **No External Dependencies**
- Only uses Python standard library
- Reduces attack surface and dependency vulnerabilities

✅ **Token Handling**
- Token read from environment variable
- Never logged or exposed in output
- Passed securely to HTTPS API only

✅ **No Secrets in Output**
- Commit hashes and messages only (from public commits)
- No credentials or tokens in generated files

## Contributing

To extend the categorization logic:

1. Edit `CATEGORY_PATTERNS` in `GitCommitParser.categorize_commits()`
2. Add new regex patterns for your conventions
3. Test with sample commits

## License

This script is part of the microsphere-logging project and follows the same license.

## See Also

- [GitHub Models API Documentation](https://github.com/marketplace/models)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
- [Git Tags Reference](https://git-scm.com/book/en/v2/Git-Basics-Tagging)
