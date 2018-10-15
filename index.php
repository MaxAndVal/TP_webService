<?php
// web/index.php
$loader = require_once __DIR__.'./WebService/vendor/autoload.php';
require __DIR__.'./WebService/src/app.php';
//require __DIR__.'/../src/routes.php';
require __DIR__.'./WebService/config/dev.php';

$app->run();