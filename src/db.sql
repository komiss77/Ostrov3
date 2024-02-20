-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Хост: localhost:3306
-- Время создания: Май 04 2021 г., 17:51
-- Версия сервера: 8.0.23-0ubuntu0.20.04.1
-- Версия PHP: 7.4.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `home`
--

-- --------------------------------------------------------

--
-- Структура таблицы `claims`
--

CREATE TABLE `claims` (
  `cLoc` int NOT NULL,
  `name` varchar(32) NOT NULL DEFAULT '',
  `factionId` int NOT NULL,
  `claimOrder` int NOT NULL,
  `userAcces` varchar(288) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `roleAcces` int NOT NULL DEFAULT '0',
  `relationAcces` int NOT NULL DEFAULT '0',
  `flags` int NOT NULL DEFAULT '0',
  `wildernesAcces` tinyint(1) NOT NULL DEFAULT '0',
  `structureData` int NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `data`
--

CREATE TABLE `data` (
  `id` int NOT NULL,
  `name` varchar(16) NOT NULL,
  `homes` varchar(512) NOT NULL DEFAULT '',
  `world_pos` varchar(512) NOT NULL DEFAULT '',
  `fly` tinyint(1) NOT NULL DEFAULT '0',
  `flyspeed` tinyint NOT NULL DEFAULT '-1',
  `walkspeed` tinyint NOT NULL DEFAULT '-1',
  `pvp` tinyint(1) NOT NULL DEFAULT '1',
  `pweather` tinyint(1) NOT NULL DEFAULT '-1',
  `rtime` tinyint(1) NOT NULL DEFAULT '0',
  `ptime` tinyint NOT NULL DEFAULT '-1',
  `bplace` int NOT NULL DEFAULT '0',
  `bbreak` int NOT NULL DEFAULT '0',
  `mobkill` int NOT NULL DEFAULT '0',
  `monsterkill` int NOT NULL DEFAULT '0',
  `pkill` int NOT NULL DEFAULT '0',
  `dead` int NOT NULL DEFAULT '0',
  `kits` varchar(535) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `disbaned`
--

CREATE TABLE `disbaned` (
  `factionId` int NOT NULL,
  `factionName` varchar(48) NOT NULL,
  `created` int NOT NULL,
  `disbaned` int NOT NULL,
  `reason` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `errors`
--

CREATE TABLE `errors` (
  `id` int NOT NULL,
  `msg` varchar(512) NOT NULL,
  `stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `factions`
--

CREATE TABLE `factions` (
  `factionId` int NOT NULL,
  `factionName` varchar(32) NOT NULL DEFAULT '',
  `data` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `econ` varchar(128) NOT NULL DEFAULT '',
  `acces` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `rolePerms` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `tagLine` varchar(64) NOT NULL DEFAULT '',
  `createTimestamp` int NOT NULL DEFAULT '0',
  `lastActivity` int NOT NULL DEFAULT '0',
  `logo` varchar(256) NOT NULL DEFAULT '',
  `home` varchar(64) NOT NULL DEFAULT '',
  `flags` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `fplayers`
--

CREATE TABLE `fplayers` (
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `factionId` int NOT NULL DEFAULT '-1',
  `joinedAt` int NOT NULL DEFAULT '0',
  `settings` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `perm` varchar(512) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `logs`
--

CREATE TABLE `logs` (
  `id` int NOT NULL,
  `factionId` int NOT NULL,
  `type` enum('Информация','Порядок','Предупреждение','Ошибка') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Информация',
  `msg` varchar(256) NOT NULL,
  `timestamp` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `moneyOffline`
--

CREATE TABLE `moneyOffline` (
  `id` int NOT NULL,
  `name` varchar(16) NOT NULL,
  `value` int NOT NULL,
  `who` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `protectionInfo`
--

CREATE TABLE `protectionInfo` (
  `cLoc` int NOT NULL,
  `sLoc` int NOT NULL,
  `owner` text NOT NULL,
  `users` text NOT NULL,
  `validTo` int NOT NULL,
  `autoCloseDelay` tinyint(1) NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `relations`
--

CREATE TABLE `relations` (
  `pairKey` int NOT NULL,
  `id1` int NOT NULL,
  `id2` int NOT NULL,
  `relation` enum('Нейтралитет','Доверие','Союз','Война') NOT NULL DEFAULT 'Нейтралитет',
  `wishFrom` int NOT NULL DEFAULT '0',
  `timestamp` int NOT NULL DEFAULT '0',
  `relationWish` enum('Нейтралитет','Доверие','Союз','Война') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Нейтралитет'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `stats`
--

CREATE TABLE `stats` (
  `factionId` int NOT NULL,
  `claims` int NOT NULL DEFAULT '0',
  `stars` int NOT NULL DEFAULT '0',
  `power` int NOT NULL DEFAULT '0',
  `useCreative` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `turrets`
--

CREATE TABLE `turrets` (
  `id` int NOT NULL,
  `factionId` int NOT NULL,
  `cLoc` int NOT NULL,
  `tLoc` int NOT NULL,
  `settings` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `warps`
--

CREATE TABLE `warps` (
  `id` int NOT NULL,
  `name` varchar(16) NOT NULL,
  `type` varchar(16) NOT NULL DEFAULT 'player',
  `owner` varchar(16) NOT NULL DEFAULT '',
  `descr` varchar(128) NOT NULL DEFAULT '',
  `loc` varchar(64) NOT NULL DEFAULT '',
  `open` tinyint(1) NOT NULL DEFAULT '1',
  `need_perm` tinyint(1) NOT NULL DEFAULT '0',
  `use_cost` int NOT NULL DEFAULT '0',
  `use_counter` int NOT NULL DEFAULT '0',
  `create_time` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `wars`
--

CREATE TABLE `wars` (
  `warId` int NOT NULL,
  `fromId` int NOT NULL,
  `toId` int NOT NULL,
  `fromName` varchar(32) NOT NULL DEFAULT '',
  `toName` varchar(32) NOT NULL DEFAULT '',
  `declareAt` int NOT NULL,
  `endAt` int NOT NULL,
  `provision` int NOT NULL,
  `reparation` int NOT NULL,
  `contribution` int NOT NULL,
  `totalDamage` int NOT NULL DEFAULT '0',
  `totalRegen` int NOT NULL DEFAULT '0',
  `totalKills` int NOT NULL DEFAULT '0',
  `totalTurrets` int NOT NULL DEFAULT '0',
  `totalUnclaim` int NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `claims`
--
ALTER TABLE `claims`
  ADD PRIMARY KEY (`cLoc`),
  ADD UNIQUE KEY `cLoc` (`cLoc`);

--
-- Индексы таблицы `data`
--
ALTER TABLE `data`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Индексы таблицы `disbaned`
--
ALTER TABLE `disbaned`
  ADD PRIMARY KEY (`factionId`),
  ADD UNIQUE KEY `factionId` (`factionId`);

--
-- Индексы таблицы `errors`
--
ALTER TABLE `errors`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `factions`
--
ALTER TABLE `factions`
  ADD UNIQUE KEY `factionId` (`factionId`);

--
-- Индексы таблицы `fplayers`
--
ALTER TABLE `fplayers`
  ADD UNIQUE KEY `name` (`name`);

--
-- Индексы таблицы `logs`
--
ALTER TABLE `logs`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `moneyOffline`
--
ALTER TABLE `moneyOffline`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `relations`
--
ALTER TABLE `relations`
  ADD PRIMARY KEY (`pairKey`),
  ADD UNIQUE KEY `pairKey` (`pairKey`);

--
-- Индексы таблицы `stats`
--
ALTER TABLE `stats`
  ADD UNIQUE KEY `islandID` (`factionId`);

--
-- Индексы таблицы `turrets`
--
ALTER TABLE `turrets`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `warps`
--
ALTER TABLE `warps`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Индексы таблицы `wars`
--
ALTER TABLE `wars`
  ADD PRIMARY KEY (`warId`),
  ADD UNIQUE KEY `warId` (`warId`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `data`
--
ALTER TABLE `data`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `errors`
--
ALTER TABLE `errors`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT для таблицы `logs`
--
ALTER TABLE `logs`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `moneyOffline`
--
ALTER TABLE `moneyOffline`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `warps`
--
ALTER TABLE `warps`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
