<?php

// Enable PHP Error level
error_reporting(E_ALL);
ini_set('display_errors', 'On');

// Enable debug mode
$app['debug'] = true;

// Doctrine (db)
$app['db.options'] = array(
    'driver' => 'pdo_mysql',
    'host' => 'https://feeblest-grinder.000webhostapp.com/',
    'port' => '3306',
    'dbname' => 'id7542389_rickandmorty',
    'user' => 'id7542389_zigoval',
    'password' => 'Melikus&Zigoval',
);
