<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<!--
  ~ Copyright (C) 2007-2014 Crafter Software Corporation.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->
<html>
<head>
    <title>Swagger UI</title>
    <link href='https://fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'/>
    <link href='<@spring.url '/swagger-ui/css/reset.css'/>' media='screen' rel='stylesheet' type='text/css'/>
    <link href='<@spring.url '/swagger-ui/css/screen.css'/>' media='screen' rel='stylesheet' type='text/css'/>
    <link href='<@spring.url '/swagger-ui/css/reset.css'/>' media='print' rel='stylesheet' type='text/css'/>
    <link href='<@spring.url '/swagger-ui/css/screen.css'/>' media='print' rel='stylesheet' type='text/css'/>
    <script type="text/javascript" src="<@spring.url '/swagger-ui/lib/shred.bundle.js'/>"></script>
    <script src='<@spring.url '/swagger-ui/lib/jquery-1.8.0.min.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/lib/jquery.slideto.min.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/lib/jquery.wiggle.min.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/lib/jquery.ba-bbq.min.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/lib/handlebars-1.0.0.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/lib/underscore-min.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/lib/backbone-min.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/lib/swagger.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/swagger-ui.js'/>' type='text/javascript'></script>
    <script src='<@spring.url '/swagger-ui/lib/highlight.7.3.pack.js'/>' type='text/javascript'></script>

    <script type="text/javascript">
        $(function () {
            window.swaggerUi = new SwaggerUi({
                url: "<@spring.url '/api-docs'/>",
                dom_id: "swagger-ui-container"
            });
            window.swaggerUi.load();
        });
    </script>
</head>

<body class="swagger-section">
<div id="message-bar" class="swagger-ui-wrap">&nbsp;</div>
<div id="swagger-ui-container" class="swagger-ui-wrap"></div>
</body>
</html>
