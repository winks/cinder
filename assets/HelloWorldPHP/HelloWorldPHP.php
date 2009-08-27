<?php
include './HelloWorldPHPOutput.php';
include_once './HelloWorldPHPOutput.php';

class HelloWorldPHP {
    function __construct() {
        
        $hwo = new HelloWorldPHPOutput();
        $hwo->say();
    }
}
$hwp = new HelloWorldPHP();
?>