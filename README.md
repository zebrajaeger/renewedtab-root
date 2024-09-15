# Renewed Tab for Server

- This is a modified version of <https://github.com/rubenwardy/renewedtab>, a great browser plugin to have a dashboard as start page.
- After trying many dashboards for my home serer, I realized, that I need this plugin for a server.

- There is a forked and slightly modified version: <https://github.com/zebrajaeger/renewedtab-as-server-dashboard>
  - Config ist stored remote (same browser window location and port 8255, REST api)
  - For an easy integration (in a Java developer workflow...) I added a maven pom.xml, so it is self-contained (no node installation is required)

- My modified version is a submodule of this project (the 'client' folder).
  - I am not a big fan of git submodules, but with this it can (hopefully) easy maintain the renewedtab,'cause of only little modification of the code.

 