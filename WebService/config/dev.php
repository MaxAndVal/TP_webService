<?php

// Enable PHP Error level
error_reporting(E_ALL);
ini_set('display_errors', 'On');

// Enable debug mode
$app['debug'] = true;

// Doctrine (db)
$app['db.options'] = array(
    'driver' => 'pdo_mysql',
    'host' => 'https://h2-phpmyadmin.infomaniak.ch/MySQLAdmin/index.php',
    'port' => '3306',
    'dbname' => 'psqt_rickandmorty',
    'user' => 'psqt_rick',
    'password' => 'Rickandmorty26',
);
