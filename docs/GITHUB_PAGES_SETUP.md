# GitHub Pages Setup

## Workflow

GitHub Actions is configured in:

```text
.github/workflows/deploy-pages.yml
```

It builds the Angular frontend and deploys it to GitHub Pages on every push to `main`.

## Enable Pages Once

1. Open the repository:

```text
https://github.com/nikkias/Banking-App
```

2. Open `Settings`.
3. Open `Pages`.
4. Under `Build and deployment`, choose `Source: GitHub Actions`.
5. Push or rerun the workflow `Deploy Angular to GitHub Pages`.

Expected URL:

```text
https://nikkias.github.io/Banking-App/
```

## Important Scope

GitHub Pages hosts static Angular files only. It does not run Spring Boot or PostgreSQL.

For a fully interactive online banking demo, deploy the backend separately and set the production Angular API URL to that public backend URL. The current local/API configuration uses `/api`, which works with the Nginx container proxy but not automatically with GitHub Pages.

Recommended split:

```text
Angular showcase: GitHub Pages
Spring Boot API: Render or Azure Container Apps
PostgreSQL: Render Postgres or Azure Database for PostgreSQL
```

## Check Deployment

After pushing:

1. Open the repository `Actions` tab.
2. Open workflow `Deploy Angular to GitHub Pages`.
3. Wait until `build` and `deploy` are green.
4. Open the Pages URL.

If the repository is private, GitHub Pages availability depends on the account/organization plan. A public repository works with the standard free Pages setup.
