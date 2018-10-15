<?php

namespace App\Users\Entity;

class User
{
    protected $id;

    protected $nom;

    protected $email;

    public function __construct($id, $nom, $email)
    {
        $this->id = $id;
        $this->prenom = $prenom;
        $this->nom = $email;
    }

    public function setId($id)
    {
        $this->id = $id;
    }

    public function setNom($nom)
    {
        $this->nom = $nom;
    }

    public function setPrenom($email)
    {
        $this->email = $email;
    }

    public function getId()
    {
        return $this->id;
    }
    public function getEmail()
    {
        return $this->email;
    }
    public function getNom()
    {
        return $this->nom;
    }

    public function toArray()
    {
        $array = array();
        $array['id'] = $this->id;
        $array['nom'] = $this->nom;
        $array['email'] = $this->email;

        return $array;
    }
}
