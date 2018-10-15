<?php
// web/index.php
$loader = require_once __DIR__.'/../vendor/autoload.php';
require __DIR__.'/app.php';
require __DIR__.'/routes.php';

$app->run();
