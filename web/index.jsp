<%-- 
    Document   : index
    Created on : 20/03/2021, 16:03:30
    Author     : willi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
        <link rel="stylesheet" href="style.css" />
        <link rel="stylesheet" href="estilos/login.css" />
        <link rel="shortcut icon" type="image/x-icon" href="img/forms.svg" />
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    </head>
    <body>
        <%
            HttpSession actual = request.getSession();
            if (actual.getAttribute("USUARIO") == null) {
        %>
        <div id="blurry-filter"></div>
        <header>
            <div>
                <article id="title"><span class="parent">login</span><br /><span class="name">WF<a style="display:contents;text-decoration: none;color:#84d404;"><img src="img/forms.svg" style="width: 25px;height: 25px;"></a>RMS</span>
                </article>
                <article id="reference"><a style="display:contents;text-decoration: none;color:#84d404;"><span style="font-size:18px;">Crea y comparte formularios</span></a></article>
            </div>
        </header>
        <div class="page">
            <div class="container">
                <div class="left">
                    <div class="eula" style="margin: 40px 40px 10px 40px;"><center><img src="img/checklist.svg" width="50%" height="50%"></center></div>
                    <div class="login" style="margin:  5px !important;"><h1 style="width:100%;font-size: 28px;text-align: center; margin:  0px !important;">LOGIN</h1>
                        <p style="width:100%;font-size: 12px;text-align: center; margin:  0px !important;">Ingresa tus credenciales para lograr ver los formularios que has creado</p></div>

                </div>
                <div class="right">
                    <div class="form">
                        <form id="formularioLogin" method="GET" action="Login">
                            <label for="usuario">Usuario</label>
                            <input type="text" id="usuario" required>
                            <label for="password">Password</label>
                            <input type="password" id="password" required>
                            <input type="submit" id="submit" value="INGRESAR">
                        </form>
                    </div>
                </div>
            </div>

        </div>
        <script>
            window.onload = function () {
                $("#formularioLogin").bind("submit", function () {
                    var usuario = document.getElementById("usuario").value;
                    var password = document.getElementById("password").value;
                    $.ajax({
                        type: $(this).attr("method"),
                        url: $(this).attr("action"),
                        data: {usuario: usuario, password: password},
                        beforeSend: function () {
                        },
                        complete: function (data) {
                        },
                        success: function (data) {
                            if (data.includes("ERROR"))
                            {
                                alert("Las credenciales no existen en la base de datos");
                            } else {
                                window.location = "forms.jsp";
                            }
                        },
                        error: function (data) {
                            alert("Problemas al tratar de enviar el formulario");
                        }
                    });
                    return false;
                });
            };
        </script>
        <%} else {
                response.sendRedirect("http://localhost:80/WForms/forms.jsp");
            }%>
    </body>
</html>
