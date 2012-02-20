# MCNSAChat2
---
**MCNSAChat2** is the rebirth of the MCNSAChat plugin that has helped [mcnsa](http://mcnsa.com) manage chat for the last half a year. There were a few problems with the old chat - it used old permissions, the code was horribly sloppy, it was somewhat inefficient, etc. This version seeks to fix all those problems and introduce some new functionality that will keep our server running happily for the foreseeable future.

## Configuration
---
Here is the default configuration file:

	# local-chat-radius: radius of local chat channel (in blocks)
	# default-channel: default chat channel that new users will join
	# default-colour: default new channel colour
	# announce-timeouts: announce timeouts to the entire server?
	# chat-format: the format chat will be printed in
	# emote-format: the format emotes will be printed in
	# 	** for the above 2, use the following substitutions:
	#		%channel	the players channel
	#		%prefix		the players prefix (from PermissionsEx)
	#		%suffix		the players suffix (from PermissionsEx)
	#		%player		the players name
	#		%message	the message
	# spam:
	#     limit: number of messages sent
	#     period: within the given time period
	# hard channels
	#	channel name:
	#		colour: <colour name>
	#		alias: <alias> (for example, alias = "g", type "/g" to switch to channel G
	#		local: <true/false> (optional)
	#		broadcast: <true/false> (optional)
	#		permission: <permission node> permission node to get into the channel (optional)
	#						final permission string will be: "mcnsachat2.channel.<permission node>"
	#		listeners: <permission node> permission node to automatically listen in on the channel (optional)
	#						final permission string will be: "mcnsachat2.listen.<permission node>"
	local-chat-radius: 200
	default-channel: G
	default-colour: grey
	announce-timeouts: true
	chat-format: "<%channel&f> [%prefix%suffix&f] %player: &7%message"
	emote-format: "<%channel&f> [%prefix%suffix&f] *&7%player %message"
	spam:
	  limit: 5
	  period: 3
	hard-channels:
	  G:
	    colour: grey
	    alias: g
	  L:
	    colour: pink
	    alias: lo
	    local: true
	  Broadcast:
	    colour: purple
	    alias: bc
	    permission: broadcast
	    broadcast: true
	  MOD:
	    colour: purple
	    alias: m
	    permission: mod
	    listeners: mod
	  ADMIN:
	    colour: green
	    alias: a
	    permission: admin
	    listeners: admin

## Commands & Permissions
---
### Command Permissions
|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|/chelp||Provides a help menu for all chat commands you can use|
|/me \<emote\>|mcnsachat2.me|Emotes your \<emote\>|
|/clist|mcnsachat2.list|Lists all chat channels that you have access to|
|/c \<channel\>||Changes your channel to \<channel\> (creating the channel if it does not already exist)|
|/ccolour <colour>|mcnsachat2.setcolour|Changes the colour of the current channel|
|/cs|mcnsachat2.seeall|Allows you to see ALL chat from ALL channels|
|/ct <player>|mcnsachat2.timeout|Toggle a player's timeout status|
|/csearch \<player\>|mcnsachat2.search|searches for what channel \<player\> is in|
|/cwho|mcnsachat2.who|list everyone who is in the channel|
|/cvc||toggle VoxelChat chat bubbles on or off|
|/cverbose \<all:some:none\>||toggle how verbose chat is (hide/show game join/leave notifications, hide/show channel join/leave notifications, etc|
|/cmute \<player\>|mcnsachat2.mute|toggles whether you have personally muted \<player\>|
|/clock [player]|mcnsachat2.lock|toggle whether a player is locked from changing channels (or lists who is currently locked if no arguments are given)|
|/cpoof|mcnsachat2.poof|toggles whether you are chat poofed or not|
|/rtd [# sides]|mcnsachat2.rtd|rolls an [#]-sided die (default: 6)|
|/crave|mcnsachat2.rave|toggles rave mode in your channel|
|/confusion|mcnsachat2.confusion|toggles confusion mode in your channel|

### Verbosity Levels ##
|**Level**|**Effect**|
|:--------|:---------|
|All|Shows game join / leave notifications, shows channel join / leave notifications, shows player timeout broadcasts, shows channel join message, shows who is in channel upon entering it, shows channel colour change notifications, shows per-player mute notifications, shows channel lock notifications, shows channel move notifications|
|Some|Shows game join / leave notifications, shows channel join message, shows per-player mute notifications, shows channel lock notifications, shows channel move notifications|
|None|Shows none of the above|

### As Of Yet Unimplemented Commands
|**Command**|**Effect**|
|:----------|:---------|
|/cht \<player\> \<time\>|automatically timed timeouts|
|/chlisten \<channel\>|toggle listening on a specific channel|

### General Permissions
|**Permission Node**|**Effect**|
|:------------------|:---------|
|mcnsachat2.colour|Allows a player to use colour tags in their message (ie: &a, &c, etc)|
|mcnsachat2.seepoofed|Allows a player to see players who are poofed|
|mcnsachat2.ignorespam|Ignore spam checks on players who have this permission|

## TODO List ##
* Implement '*' player selector
* Implement auto-timed timeouts
* Implement per-channel listening
* Potentially make verbosity levels finer?
* Add multiple aliases to commands (/c, /ch, do the same thing, etc)
* Add configuration lines for player joined / left the game messages
* Add / fix "<player> has joined the channel messages"
* Colours, names, ranks in chat bubbles?
* Hard list of players who have VoxelChat bubbles, only allow manual enabling for them?
* Add fun commands like /rtd, etc