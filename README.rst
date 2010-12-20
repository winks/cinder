CINDER - Continous INtegration Data in Eclipse Represented
==========================================================

Cinder is a plug-in that lets you see the reports and errors from your 
Continuous Integration system (CruiseControl, Hudson, Bamboo) directly 
inside your eclipse-based IDE (Eclipse, Zend Studio, Aptana, etc.).

Installation
============

Short Version:
~~~~~~~~~~~~~~
- Install from
    http://opensource.mayflower.de/cinder
- Show the view:
    Window -> Show View -> Other

Long Version:
~~~~~~~~~~~~~
The plugin can be installed by first adding the according
Eclipse Update Site to Eclipse by following these steps:

- Help -> Install New Software... -> Work with: http://opensource.mayflower.de/cinder -> Add...

And then selecting Cinder - CinderFeature Version x.y.z
and clicking on "Next >" and following the instructions in the wizard window.

After installation, the Cinder View can be displayed by choosing

Window -> Show View -> Other

then browsing to

Cinder -> Cinder View

Changelog
=========

0.1.6   21.12.2010
    * Fixed: X button in "Load from URL" does not cancel the operation
    * Fixed: Hide selected markers
    * Fixed: Bad XML input performance
    * Fixed: Bad ItemManager clearing performance
    * Fixed: Bad performance when setting status to many items
