package br.com.zenix.core.plugin.data.mysql.queries;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public enum CoreQueries {
	
	DATABASE("zenix"),
    
    ACCOUNTS("SELECT * FROM `global_accounts` WHERE `id`=?"),
	
    ACCOUNT_QUERY("SELECT * FROM `global_accounts` WHERE `unique_id`=?"),
    ACCOUNT_INSERT("INSERT INTO `global_accounts` (`unique_id`, `nick`, `group`, `group_time`, `first_login`, `last_login`) VALUES (?, ?, ?, ?, ?, ?);"),
    ACCOUNT_UPDATE("UPDATE `global_accounts` SET `nick`=?, `ip`=?, `group`=?, `group_time`=?, `last_login`=? WHERE `id`=?;"),
    ACCOUNT_UPDATE_RANK("UPDATE `global_accounts` SET `group`=?, `group_time`=? WHERE `id`=?;"),
    
    ACCOUNT_SECONDARY_RANK_QUERY("SELECT * FROM `player_permissions` WHERE `player`=?"),
    ACCOUNT_SECONDARY_GROUP_DELETE("DELETE FROM `player_permissions` WHERE `group`=? AND `player`=?;"),
    ACCOUNT_INSERT_SECONDARY_RANK("INSERT INTO `player_permissions` (`player`, `group`, `group_time`) VALUES (?, ?, ?);"),
    ACCOUNT_UPDATE_SECONDARY_RANK("UPDATE `player_permissions` SET `group`=?, `group_time`=? WHERE `player`=?;"),
	
    ACCOUNT_GLOBAL_DATA_QUERY("SELECT * FROM `global_data` WHERE `player`=?"),
    ACCOUNT_GLOBAL_DATA_QUERY_BY_TYPE("SELECT * FROM `global_data` WHERE `player`=? AND `type`=?;"),
    ACCOUNT_GLOBAL_DATA_INSERT("INSERT INTO `global_data` (`player`,`type`,`value`) VALUES (?, ?, ?);"),
    ACCOUNT_GLOBAL_DATA_UPDATE("UPDATE `global_data` SET `value`=? WHERE `player`=? AND `type`=?;"),
    
    ACCOUNT_GLOBAL_DAILY_DATA_QUERY("SELECT * FROM `global_data_daily` WHERE `player`=?"),
    ACCOUNT_GLOBAL_DAILY_DATA_QUERY_BY_TYPE("SELECT * FROM `global_data_daily` WHERE `player`=? AND `type`=? AND `time`=?;"),
    ACCOUNT_GLOBAL_DAILY_DATA_INSERT("INSERT INTO `global_data_daily` ( `value`, `player`, `type`, `time`) VALUES (?, ?, ?, ?);"),
    ACCOUNT_GLOBAL_DAILY_DATA_UPDATE("UPDATE `global_data_daily` SET `value`=? WHERE `player`=? AND `type`=? AND `time`=?;"),
    
    ACCOUNT_XP_STATUS_QUERY("SELECT * FROM `global_xp` WHERE `id`=?"),
    ACCOUNT_XP_STATUS_INSERT("INSERT INTO `global_xp` (`id`) VALUES (?);"),
    ACCOUNT_XP_STATUS_UPDATE("UPDATE `global_xp` SET `xp`=?, `start`=?, `end`=?, `running`=? WHERE `id`=?;"),
	
    ACCOUNT_PERMISSIONS_SELECT("SELECT * FROM `global_permissions` WHERE `owner`=? ORDER BY `active` DESC, `time` DESC;"),
    ACCOUNT_PERMISSIONS_INSERT("INSERT INTO `global_permissions`(`permission`,`owner`,`active`,`time`) VALUES (?, ?, ?, ?);"),
    ACCOUNT_PERMISSIONS_DELETE("DELETE FROM `global_permissions` WHERE `permission`=? AND `owner`=?;"),
	
    RANKS_SELECT("SELECT * FROM `global_groups`;"),
    RANKS_PERMISSIONS_SELECT("SELECT * FROM `global_permissions` ORDER BY `group` DESC;"),
    RANKS_PERMISSIONS_SELECT_BY_GROUP("SELECT * FROM `global_permissions` WHERE `group`=? ORDER BY `group` DESC;"),
    RAMKS_PERMISSIONS_UPDATE("UPDATE `global_permissions` SET `active`=? WHERE `permission`=? AND `group`=?;"),
    RANKS_PERMISSIONS_DELETE("DELETE FROM `global_permissions` WHERE `permission`=? AND `group`=?;"),
    RANKS_PERMISSIONS_INSERT("INSERT INTO `global_permissions`(`permission`,`owner`,`group`,`active`,`time`) VALUES (?, ?, ?, ?, ?);"),
    
    RANKS_INSERT("INSERT INTO `global_groups` (`name`, `tag`) VALUES (?, ?);"),
    RANKS_UPDATE("UPDATE `global_groups` SET `tag`=?, `default`=? WHERE `id`=?;"),
    RANKS_DEFAULT_UPDATE_PLAYERS("UPDATE `global_accounts` SET `group`=? WHERE `group`=?;"),
    RANKS_DEFAULT_UPDATE("UPDATE `global_groups` SET `default`=? WHERE `id`=?;"),
    RANKS_DEFAULT_ALL_UPDATE("UPDATE `global_groups` SET `default`=?;"),
    RANKS_DELETE("DELETE FROM `global_groups` WHERE `id`=?;"),
    
    LANG_DATA_QUERY("SELECT * FROM `global_lang`;"),
    
    MATCH_STATUS_SELECT_ALL("SELECT * FROM `match_status`;"),
    MATCH_STATUS_SELECT("SELECT * FROM `match_status` WHERE `id`=?;"),
    MATCH_STATUS_INSERT("INSERT INTO `match_status` (`start`, `type`, `end`) VALUES (?, ?, ?);"),
    MATCH_STATUS_UPDATE("UPDATE `match_status` SET `start`=? ,`end`= ? WHERE `id`=?;"),
	
    MATCH_PLAYER_SELECT("SELECT * FROM `match_players` WHERE `match`=?;"),
    MATCH_PLAYER_INSERT("INSERT INTO `match_players` (`match`, `player`, `kits`, `position`) VALUES (?, ?, ?, ?);"),
    MATCH_PLAYER_UPDATE("UPDATE `match_players` SET `kits`=?, `position`=? WHERE `player`=?;"),
	
    MATCH_KILLS_SELECT("SELECT * FROM `match_kills` WHERE `match`=?;"),
    MATCH_KILLS_INSERT("INSERT INTO `match_kills` (`match`, `killer`, `dead`, `when`, `respawn`) VALUES (?, ?, ?, ?, ?);"),
	
    TAGS_SELECT("SELECT * FROM `global_tags`;"),
    TAGS_INSERT("INSERT INTO `global_tags`(`name`, `prefix`, `color`, `order`) VALUES (?, ?, ?, ?);"),
    TAGS_UPDATE("UPDATE `global_tags` SET `prefix`=?, `color`=?, `order`=? WHERE `id`=?;"),
    TAGS_DELETE("DELETE FROM `global_tags` WHERE `id`=?;"),
	
    PUNISH_SELECT("SELECT * FROM `global_punish` ORDER BY `expire` DESC;"),
    PUNISH_PUNISHED_SELECT_ACTIVE("SELECT * FROM `global_punish` WHERE `punished`=? AND `active`=?;"),
    
    AUTHENTICATOR_ACTIVE("SELECT * FROM `auth` WHERE `pass`=?;"),
    AUTHENTICATOR_GET("SELECT * FROM auth WHERE nick= ?"),
    AUTHENTICATOR_DELETE("DELETE FROM `auth` WHERE `nick`=?;"),
    
    PUNISH_PUNISHED_SELECT("SELECT * FROM `global_punish` WHERE `punished`=?;"),
    PUNISH_PUNISHED_INSERT("INSERT INTO `global_punish` (`punished`, `staff`, `start`, `expire`, `motive`, `active`, `type`) VALUES (?, ?, ?, ?, ?, ?, ?);"),
    PUNISH_PUNISHED_UPDATE("UPDATE `global_punish` SET `active`=? WHERE `id`=?;"),
    PUNISH_PUNISHED_DELETE("DELETE FROM `global_punish` WHERE `id`=?;"),
	
    NAME_FETCHER_SELECT("SELECT * FROM `global_accounts` WHERE `%index%`=?;"),
	
    GLOBAL_KITS_SELECT("SELECT * FROM `global_kits` WHERE `type`=? AND `name`=?;"),
    GLOBAL_KITS_INSERT("INSERT INTO `global_kits` (`name`, `type`) VALUES (?, ?);"),
    
    SERVER_CONFIG_SELECT_ALL("SELECT * FROM `server_config`;"),
    SERVER_CONFIG_SELECT("SELECT * FROM `server_config` WHERE `key`= ?;"),
    SERVER_CONFIG_UPDATE("UPDATE `server_config` SET `value`=? WHERE `key`= ?;"),
	
    TABLE_GLOBAL_TAGS("CREATE TABLE IF NOT EXISTS `global_tags` (`id` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(16) NOT NULL, `prefix` VARCHAR(16) NOT NULL DEFAULT '§7', `color` VARCHAR(16) NOT NULL DEFAULT '§7', `order` INT NOT NULL DEFAULT 1000, PRIMARY KEY (`id`), UNIQUE INDEX `id_UNIQUE` (`id` ASC), UNIQUE INDEX `name_UNIQUE` (`name` ASC))ENGINE = InnoDB;"),
    TABLE_GLOBAL_GROUPS("CREATE TABLE IF NOT EXISTS `global_groups` (`id` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(16) NOT NULL, `tag` INT NOT NULL, `default` TINYINT(1) NOT NULL DEFAULT 0, PRIMARY KEY (`id`, `tag`), UNIQUE INDEX `id_UNIQUE` (`id` ASC), UNIQUE INDEX `name_UNIQUE` (`name` ASC), INDEX `group_tag_id` (`tag` ASC), CONSTRAINT `group_tag_id` FOREIGN KEY (`tag`) REFERENCES `global_tags` (`id`) ON DELETE CASCADE ON UPDATE CASCADE)ENGINE = InnoDB;"),
    TABLE_GLOBAL_ACCOUNTS("CREATE TABLE IF NOT EXISTS `global_accounts` (`id` INT NOT NULL AUTO_INCREMENT, `unique_id` VARCHAR(36) NOT NULL,`nick` VARCHAR(16) NOT NULL, `group` INT NOT NULL,`group_time` BIGINT(20) NOT NULL DEFAULT 1, `first_login` BIGINT(20) NOT NULL DEFAULT 1,`last_login` BIGINT(20) NOT NULL DEFAULT 1, `ip` VARCHAR(15) NOT NULL DEFAULT ' ', PRIMARY KEY (`id`, `group`), UNIQUE INDEX `id_UNIQUE` (`id` ASC), UNIQUE INDEX `uuid_UNIQUE` (`unique_id` ASC), INDEX `account_group_id` (`group` ASC), CONSTRAINT `account_group_id` FOREIGN KEY (`group`) REFERENCES `global_groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;"),
    TABLE_GLOBAL_PERMISSIONS("CREATE TABLE IF NOT EXISTS `global_permissions` (`id` INT NOT NULL AUTO_INCREMENT, `permission` VARCHAR(32) NOT NULL,`owner` INT NOT NULL DEFAULT 1,`group` INT NOT NULL DEFAULT 1,`active` INT NOT NULL DEFAULT 0,`time` BIGINT(20) NOT NULL DEFAULT 1,  PRIMARY KEY (`id`), UNIQUE INDEX `id_UNIQUE` (`id` ASC)) ENGINE = InnoDB;"),
    TABLE_PLAYER_GROUP("CREATE TABLE IF NOT EXISTS `player_permissions` (`id` INT NOT NULL AUTO_INCREMENT, `player` INT NOT NULL,`group` INT NOT NULL,`group_time` BIGINT(20) NOT NULL DEFAULT 1, 	PRIMARY KEY (`id`, `player`, `group`),	 UNIQUE INDEX `id_UNIQUE` (`id` ASC),	 INDEX `group_id` (`group` ASC),	  CONSTRAINT `player_id` FOREIGN KEY (`player`) REFERENCES `global_accounts` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION, CONSTRAINT `group_id` FOREIGN KEY (`group`) REFERENCES `global_groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;"),
    TABLE_GLOBAL_KITS("CREATE TABLE IF NOT EXISTS `global_kits` (`id` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(45) NOT NULL, `type` INT NOT NULL DEFAULT 1, PRIMARY KEY (`id`), UNIQUE INDEX `id_UNIQUE` (`id` ASC))ENGINE = InnoDB;"),
    TABLE_GLOBAL_XP("CREATE TABLE IF NOT EXISTS `global_xp` (`id` INT NOT NULL, `xp` INT NOT NULL DEFAULT 0, `start` VARCHAR(64) NOT NULL, `end` VARCHAR(64) NOT NULL, `running` INT NOT NULL DEFAULT 0, PRIMARY KEY (`id`), UNIQUE INDEX `id_UNIQUE` (`id` ASC), CONSTRAINT `account_xp_id` FOREIGN KEY (`id`) REFERENCES `global_accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE)ENGINE = InnoDB;"),
    TABLE_GLOBAL_SKINS("CREATE TABLE IF NOT EXISTS `global_skins` (`id` INT NOT NULL AUTO_INCREMENT, `unique_id` VARCHAR(36) NOT NULL,`value` BLOB NOT NULL, `signature` BLOB NOT NULL, `time` BIGINT(20) NOT NULL, PRIMARY KEY (`id`), UNIQUE INDEX `id_UNIQUE` (`id` ASC), UNIQUE INDEX `unique_id_UNIQUE` (`unique_id` ASC), UNIQUE INDEX `time_UNIQUE` (`time` ASC))ENGINE = InnoDB;"),
    TABLE_GLOBAL_LANG("CREATE TABLE IF NOT EXISTS `global_lang` ( `id` INT NOT NULL AUTO_INCREMENT,`lang` INT NOT NULL, `key` VARCHAR(64) NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY (`id`), UNIQUE INDEX `id_UNIQUE` (`id` ASC)) ENGINE = InnoDB;"),
    TABLE_GLOBAL_PUNISH("CREATE TABLE IF NOT EXISTS `global_punish` (`id` INT NOT NULL AUTO_INCREMENT, `punished` INT NOT NULL,`staff` INT NOT NULL,`start` BIGINT(20) NOT NULL DEFAULT 1, `expire` BIGINT(20) NOT NULL DEFAULT 1, `motive` TEXT(256) NOT NULL, `active` TINYINT(1) NOT NULL DEFAULT 0, `type` TINYINT(10) NOT NULL DEFAULT 0, PRIMARY KEY (`id`, `punished`, `staff`), UNIQUE INDEX `id_UNIQUE` (`id` ASC), INDEX `punished_id` (`punished` ASC), INDEX `staff_id` (`staff` ASC), CONSTRAINT `punished_id` FOREIGN KEY (`punished`) REFERENCES `global_accounts` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION, CONSTRAINT `staff_id` FOREIGN KEY (`staff`) REFERENCES `global_accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE = InnoDB;"),
    TABLE_MATCH_STATUS("CREATE TABLE IF NOT EXISTS `match_status` (`id` INT NOT NULL AUTO_INCREMENT, `start` INT NOT NULL DEFAULT 0, `end` INT NOT NULL DEFAULT 0, `type` INT NOT NULL DEFAULT 0, PRIMARY KEY (`id`), UNIQUE INDEX `id_UNIQUE` (`id` ASC))ENGINE = InnoDB;"),
    TABLE_MATCH_PLAYERS("CREATE TABLE IF NOT EXISTS `match_players` (`match` INT NOT NULL, `player` INT NOT NULL, `kits` TEXT NOT NULL, `position` INT NOT NULL DEFAULT 0, PRIMARY KEY (`match`, `player`), INDEX `match_player_id` (`player` ASC), CONSTRAINT `match_status_id` FOREIGN KEY (`match`) REFERENCES `match_status` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION, CONSTRAINT `player_global_accounts_id` FOREIGN KEY (`player`) REFERENCES `global_accounts` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)ENGINE = InnoDB;"),
    TABLE_MATCH_KILLS("CREATE TABLE IF NOT EXISTS `match_kills` (`match` INT NOT NULL, `killer` INT NOT NULL, `dead` INT NOT NULL,`when` INT NOT NULL, `respawn` TINYINT(1) NOT NULL DEFAULT 0)ENGINE = InnoDB;"),
    
    TABLE_GLOBAL_DATA_DAILY("CREATE TABLE IF NOT EXISTS `global_data_daily` ( `id` INT NOT NULL AUTO_INCREMENT, `player` INT NOT NULL, `type` INT NOT NULL, `value` INT NOT NULL DEFAULT 0, `time` DATE NULL, PRIMARY KEY (`id`, `player`), UNIQUE INDEX `id_UNIQUE` (`id` ASC), CONSTRAINT `fk_global_data_daily_global_accounts1` FOREIGN KEY (`player`) REFERENCES `global_accounts` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION);"),
    TABLE_GLOBAL_DATA("CREATE TABLE IF NOT EXISTS `global_data` ( `id` INT NOT NULL AUTO_INCREMENT, `player` INT NOT NULL, `type` INT NOT NULL, `value` INT NOT NULL DEFAULT 0, PRIMARY KEY (`id`, `player`), UNIQUE INDEX `id_UNIQUE` (`id` ASC),CONSTRAINT `fk_global_data_global_accounts1` FOREIGN KEY (`player`) REFERENCES `global_accounts` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION);"),
    
    TABLE_SERVER_CONFIG("CREATE TABLE IF NOT EXISTS `server_config` (`id` INT NOT NULL AUTO_INCREMENT, `key` VARCHAR(45) NOT NULL, `value` VARCHAR(45) NOT NULL, PRIMARY KEY (`id`), UNIQUE INDEX `idserver_config_UNIQUE` (`id` ASC), UNIQUE INDEX `key_UNIQUE` (`key` ASC)) ENGINE = InnoDB;"),
    TABLE_SERVER_INTERFACES("CREATE TABLE IF NOT EXISTS `server_interfaces` ( `id` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(16) NOT NULL, `group` VARCHAR(16) NOT NULL, `template` VARCHAR(16) NOT NULL, `showip` VARCHAR(64) NOT NULL, `port` INT NOT NULL DEFAULT 0, `memory` INT NOT NULL DEFAULT 0, `status` INT NOT NULL DEFAULT 0, `maxplayers` INT NOT NULL DEFAULT 0, `autostart` TINYINT(1) NOT NULL DEFAULT 0, `setup` TINYINT(1) NOT NULL DEFAULT 0, PRIMARY KEY (`id`), UNIQUE INDEX `id_UNIQUE` (`id` ASC), UNIQUE INDEX `name_UNIQUE` (`name` ASC), UNIQUE INDEX `port_UNIQUE` (`port` ASC)) ENGINE = InnoDB;"),
    
    DEFAULT_TAG_INSERT("INSERT IGNORE INTO `global_tags` (`name`) VALUES ('default');"),
    DEFAULT_GROUP_INSERT("INSERT IGNORE INTO `global_groups` (`name`, `tag`, `default`) VALUES ('default', '1', '1');"),
    DEFAULT_CONSOLE_GAMER_INSERT("INSERT IGNORE INTO `global_accounts` (`unique_id`, `nick`, `group`) VALUES ('0000000000000000000000000000000', 'Console', '1');"),
    
    LAST_ID("SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + DATABASE.toString() + "' AND TABLE_NAME=?;");

	private final String query;

	private CoreQueries(String query) {
		this.query = query;
	}

	public String toString() {
		return query;
	}
}
