<?php

class HelloWorldPHPOutput {
    function say() {
        $al = array();
        $al[] = "Hello, ";
        $al[] = "World!";
        $al[] = "\n";
        foreach($al as $a) {
            echo $a;
            echo $a;
        }
    }
}
?>