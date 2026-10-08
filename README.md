
# Velcord

> *Velocity based plugin that makes connecting discord accounts simple and secure*  

Velcord provides oppurtinity to use only one plugin on your proxy server to manage all acounts flawlessly. It's simple in use and to you can configure almost all possible functional aspect that you could thinks about.





## Features 🔍

- **Verification modes 🔐**
    - Locally handled codes
    - Discord OAuth2 auth-generated urls(and handled localy)

- **Discord member synchronization ⇄**
    - Nickname 
    - Roles

- **Full MiniMessage support ✨**

- **Live config reloads 🗘**

- **To be added 📌**
  - Command to fetch player data(last position, time since player has been seen online, recently joined server[oneblock, survival, etc]
  - if you want something to be added just contact me!






## Things you need to have in order for plugin to work 🛠️

### Discord command /verify

- LuckPerms installed 
- Discord developer account
- One discord application

### Discord OAuth2 using plugin WebApi that happens localy

- As shown above you just need to change some lines in config
- One more available port that'll be used by api


### Discord OAuth2 using external WebApi(yours!)
- Custom built web api that will exchange user token, fetch member id, invite user, update his roles upon invite and lastly change his nickname.
- Example WebApi is presented and production ready [here](https://github.com/Alekxsski/Discord_Auth)







## Installation

1. Download the latest file.
2. Place it in desired server `plugins/` folder
3. Start your velocity server
4. Stop the server
5. Edit config file placed in `plugins/velcord/config.yml`
6. Restart server
## Support 💬

**Need Help?**
- Check wiki
- Open an issue
## Acknowledgements

Velcord uses other open-source dependencies such as 
- 

## License ⚖️

[GNU GPLv3](https://choosealicense.com/licenses/agpl-3.0/)

