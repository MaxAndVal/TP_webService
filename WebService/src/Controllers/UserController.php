<?php

namespace App\Controller;

use Silex\Application;
use Symfony\Component\HttpFoundation\Request;

class UserController
{
    public function listAction(Request $request, Application $app)
    {
        $users = $app['repository.user']->getAll();
        $init = array($status = 200, $statusText = "succed");
        $myResponse = new Response($init, $users);
        echo $myResponse;
        return $myResponse;
    }

    // public function deleteAction(Request $request, Application $app)
    // {
    //     $parameters = $request->attributes->all();
    //     $app['repository.user']->delete($parameters['id']);

    //     return new Response();
    // }

    // public function editAction(Request $request, Application $app)
    // {
    //     $parameters = $request->attributes->all();

    //     $user = $app['repository.user']->getById($parameters['id']);
    //     $users = $app['repository.user']->getAll();
    //     return new Response();
    // }

    // public function saveAction(Request $request, Application $app)
    // {
    //     $parameters = $request->request->all();
    //     if ($parameters['id']) {
    //         $user = new Response();
    //     } else {
    //         $user = new Response();
    //     }

    //     return $user;
    // }

    // public function newAction(Request $request, Application $app)
    // {
    //     $users = $app['repository.user']->getAll();

    //     return new Response();
    // }
}
