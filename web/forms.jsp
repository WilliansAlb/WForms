<%-- 
    Document   : forms
    Created on : 25/03/2021, 18:12:17
    Author     : willi
--%>

<%@page import="Controladores.ControladorFormulario"%>
<%@page import="POJOS.Formulario"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Forms</title>
        <link rel="stylesheet" href="style.css" />
        <link rel="stylesheet" href="style2.css" />
        <link rel="shortcut icon" type="image/x-icon" href="img/forms.svg" />
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    </head>
    <body>

        <%
            HttpSession actual = request.getSession();
            if (actual.getAttribute("USUARIO") != null) {
        %>
        <div id="blurry-filter"></div>
        <header>
            <div>
                <article id="title"><span class="parent">tus formularios</span><br /><span class="name">WF<a href="/WForms" style="display:contents;text-decoration: none;color:#84d404;"><img src="img/forms.svg" style="width: 25px;height: 25px;"></a>RMS</span>
                </article>
                <article id="reference">
                    <a style="display:contents;text-decoration: none;color:#84d404;"><img src="img/user.svg" style="width: 25px;height: 25px;"><span style="font-size:18px;"><%out.print(actual.getAttribute("USUARIO").toString());%></span></a><div class="cerrar"><a href="http://localhost:8080/WForms/Login?logout=si" style="display:contents;text-decoration: none;"><img src="img/salir.svg" style="width: 25px;height: 25px;"><span style="font-size:12px;">CERRAR SESION</span></a></div></article>
            </div>
        </header>
        <%
            ControladorFormulario control = new ControladorFormulario();
            ArrayList<Formulario> forms = control.tusFormularios(actual.getAttribute("USUARIO").toString());
            if (forms.size() > 0) {

        %>
        <div class="paraFlex">
            <div class="contenedor">
                <%            for (int i = 0; i < forms.size(); i++) {
                        Formulario fm = forms.get(i);
                %>
                <div class="flip-card-container" style="--hue: 220">
                    <div class="flip-card">
                        <div class="card-front <%out.print(fm.getTema().toUpperCase());%>">
                            <figure>
                                <div class="img-bg"></div>
                                <img
                                    src="https://cdn.rsjoomla.com/images/products/header-image-joomla-extension-rsform.png">
                                <figcaption><%out.print(fm.getId());%></figcaption>
                            </figure>
                            <div class="datos">
                                <p><span class="nombre_param">TITULO:</span><span class="param"><%out.print(fm.getTitulo());%></span></p>
                                <p><span class="nombre_param">NOMBRE:</span><span class="param"><%out.print(fm.getNombre());%></span></p>
                                <p><span class="nombre_param">TEMA:</span><span class="param"><%out.print(fm.getTema());%></span></p>
                                <p><span class="nombre_param">ID:</span><span class="param"><%out.print(fm.getId());%></span></p>
                                <p><span class="nombre_param">FECHA CREACION:</span><span class="param"><%out.print(fm.getFecha());%></span></p>
                            </div>
                        </div>
                        <div class="card-back <%out.print(fm.getTema().toUpperCase());%>" >
                            <figure>
                                <div class="img-bg"></div>
                                <img
                                    src="https://cdn.rsjoomla.com/images/products/header-image-joomla-extension-rsform.png">
                            </figure>
                            <div style="display:grid;grid-template-columns:auto;">
                                <button style="color:yellow;background-color:#414242a6;" onclick="window.location = 'Ver?id=<%out.print(fm.getId());%>';">Ver</button>
                                <button style="color:yellow;background-color:#414242a6;" onclick="mostrarLink('http://localhost:8080/WForms/Ver?id=<%out.print(fm.getId());%>')">Obtén el link!</button>
                                <button style="color:yellow;background-color:#414242a6;" onclick="document.getElementById('link2').click()">Exportalo!</button>
                                <a id="link2" href="Descargar?id=<%out.print(fm.getId());%>" download="form_<%out.print(fm.getId());%>.form" download hidden></a>
                            </div>
                            <div class="design-container">
                                <span class="design design--1"></span>
                                <span class="design design--2"></span>
                                <span class="design design--3"></span>
                                <span class="design design--4"></span>
                                <span class="design design--5"></span>
                                <span class="design design--6"></span>
                                <span class="design design--7"></span>
                                <span class="design design--8"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <%}%>
            </div>
        </div>
        <div id="oculto" style="display:none;">
            <center>

                <div id="mensaje">
                    <label for="link">El link es: </label>
                    <div id="mlink">
                        <input id="link" type="text" disabled style="text-align:center;"><span class="popuptext" id="spam" style="display:none;">Link copiado al portapapeles!</span><button onclick="copiar();">COPIAR&#x029C9;</button>
                    </div>
                    <button onclick="ocultar(document.getElementById('oculto'));">CERRAR&#x02A2F;</button>
                </div>
            </center>
        </div>
        <script>
            function mostrarLink(link) {
                document.getElementById("oculto").style.display = "block";
                document.getElementById("link").value = link;
            }
            function ocultar(enviado) {
                enviado.style.display = "none";
            }
            function copiar() {
                var aux = document.createElement("input");
                aux.setAttribute("value", document.getElementById("link").value);
                document.body.appendChild(aux);
                aux.select();
                document.execCommand("copy");
                document.body.removeChild(aux);
                $('#spam').fadeIn();
                $('#spam').fadeOut(4000);

            }
        </script>
        <%} else {%>
        <div class="paraFlex">
            <img src="img/folder.svg" width="30%">
            <h1 style="position: absolute;backdrop-filter: blur(20px);z-index: 20;color:rgb(207, 243, 141);">Aún no has creado ningún formulario</h1>
        </div>

        <%}%>
        <%} else {
                response.sendRedirect("http://localhost:8080/WForms/");
            }%>
    </body>
</html>
