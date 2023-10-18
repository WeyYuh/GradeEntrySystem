-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Oct 19, 2023 at 03:16 AM
-- Server version: 5.5.38
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `wytest`
--
CREATE DATABASE IF NOT EXISTS `wytest` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `wytest`;

-- --------------------------------------------------------

--
-- Table structure for table `assessmentresults`
--

CREATE TABLE `assessmentresults` (
  `sid` int(11) NOT NULL,
  `aid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `score` float NOT NULL,
  `percentage` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `assessments`
--

CREATE TABLE `assessments` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `topic` varchar(10) NOT NULL,
  `category` varchar(20) NOT NULL,
  `maxScore` int(11) NOT NULL,
  `highest` float NOT NULL,
  `median` float NOT NULL,
  `mean` float NOT NULL,
  `lowest` float NOT NULL,
  `axScore` int(11) NOT NULL,
  `aScore` int(11) NOT NULL,
  `bScore` int(11) NOT NULL,
  `cScore` int(11) NOT NULL,
  `dScore` int(11) NOT NULL,
  `eScore` int(11) NOT NULL,
  `uScore` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `tid` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `class`
--

CREATE TABLE `class` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `note` varchar(256) DEFAULT NULL,
  `tid` int(11) NOT NULL,
  `stotal` int(11) NOT NULL,
  `mean` float NOT NULL,
  `median` float NOT NULL,
  `recentMean` float NOT NULL,
  `lastScore` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `logindetails`
--

CREATE TABLE `logindetails` (
  `id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(30) NOT NULL,
  `email` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `targetGrade` varchar(10) NOT NULL,
  `aspirationalGrade` varchar(10) NOT NULL,
  `currentGrade` varchar(10) NOT NULL,
  `mean` float NOT NULL,
  `median` float NOT NULL,
  `recentMean` float NOT NULL,
  `lastScore` float NOT NULL,
  `totalAssessment` int(11) NOT NULL,
  `tid` int(11) NOT NULL,
  `cid` int(11) NOT NULL,
  `badCount` int(11) NOT NULL,
  `goodCount` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `assessmentresults`
--
ALTER TABLE `assessmentresults`
  ADD UNIQUE KEY `sid` (`sid`,`aid`);

--
-- Indexes for table `assessments`
--
ALTER TABLE `assessments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `class`
--
ALTER TABLE `class`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `logindetails`
--
ALTER TABLE `logindetails`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `assessments`
--
ALTER TABLE `assessments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `class`
--
ALTER TABLE `class`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `logindetails`
--
ALTER TABLE `logindetails`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
