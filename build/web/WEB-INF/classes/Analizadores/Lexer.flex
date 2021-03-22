package Analizador;
import java_cup.runtime.Symbol;
%%
%class Lexer
%type java_cup.runtime.Symbol
%cup
%full
%line
%column
%char
%public
L=[a-zA-Z]+
D=[0-9]+
C=[@_"-"%#&]+
espacio=[ |\t|\r|\n]+
esp = [ ]+
%{
    private Symbol symbol(int type, Object value){
        return new Symbol(type, yyline, yycolumn, value);
    }
    private Symbol symbol(int type){
        return new Symbol(type, yyline, yycolumn);
    }
%}
%%

/* Inicio de mas de una solicitud */
( "ini_solicitudes" ) {return new Symbol(sym.SOLICITUDESP, yycolumn, yyline, yytext());}

/* Fin de mas de una solicitud */
( "fin_solicitudes" ) {return new Symbol(sym.FINSOLICITUDES, yycolumn, yyline, yytext());}

/* Inicio de etiqueta*/
( "<!" ) {return new Symbol(sym.INICIOE,yycolumn,yyline,yytext());}

/* Fin de etiqueta*/
( "!>" ) {return new Symbol(sym.FINE,yycolumn,yyline,yytext());}

/* Menor que */
( "<" ) {return new Symbol(sym.MENORQ,yycolumn,yyline,yytext());}

/* Mayor que */
( ">" ) {return new Symbol(sym.MAYORQ,yycolumn,yyline,yytext());}

/* O */
( "|" ) {return new Symbol(sym.OR,yycolumn,yyline,yytext());}

/* Inicio de una solicitud */
( "ini_solicitud" ) {return new Symbol(sym.INICIOS, yycolumn, yyline, yytext());}

/* Fin de una solicitud*/
( "fin_solicitud" ) {return new Symbol(sym.FINS,yycolumn,yyline,yytext());}

/* Crear usuario */
CREAR_USUARIO {return new Symbol(sym.CREARU,yycolumn,yyline,yytext());}

/* Usuario */
("USUARIO") {return new Symbol(sym.USUARIOP,yycolumn,yyline,yytext());}

/* Contraseña*/
("PASSWORD") {return new Symbol(sym.CONTRAP,yycolumn,yyline,yytext());}

/*Fecha de creación*/
("FECHA_CREACION") {return new Symbol(sym.FECHACP,yycolumn,yyline,yytext());}

/*Modificar usuario*/
("MODIFICAR_USUARIO") {return new Symbol(sym.MODIFICARU,yycolumn,yyline,yytext());}

/*Usuario antiguo*/
("USUARIO_ANTIGUO") {return new Symbol(sym.USUARIOAP,yycolumn,yyline,yytext());}

/*Usuario nuevo*/
("USUARIO_NUEVO") {return new Symbol(sym.USUARIONP,yycolumn,yyline,yytext());}

/*Contraseña nueva*/
("NUEVO_PASSWORD") {return new Symbol(sym.NUEVOP,yycolumn,yyline,yytext());}

/*Fecha modificación*/
("FECHA_MODIFICACION") {return new Symbol(sym.FECHAMP,yycolumn,yyline,yytext());}

/*Eliminar usuario*/
("ELIMINAR_USUARIO") {return new Symbol(sym.ELIMINARU,yycolumn,yyline,yytext());}

/*Login usuario*/
("LOGIN_USUARIO") {return new Symbol(sym.LOGINU,yycolumn,yyline,yytext());}

/*Palabra reservada para nuevo formulario*/
("NUEVO_FORMULARIO") {return new Symbol(sym.NUEVOFP,yycolumn,yyline,yytext());}

/*Palabra reservada para ID*/
("ID") {return new Symbol(sym.IDP,yycolumn,yyline,yytext());}

/*Palabra reservada para Titulo*/
("TITULO") {return new Symbol(sym.TITULOP,yycolumn,yyline,yytext());}

/*Palabra reservada para nombre*/
("NOMBRE") {return new Symbol(sym.NOMBREP,yycolumn,yyline,yytext());}

/*Palabra reservada para tema*/
("TEMA") {return new Symbol(sym.TEMAP,yycolumn,yyline,yytext());}

/*Palabra reservada para creacion de usuario*/
("USUARIO_CREACION") {return new Symbol(sym.USUARIOCP,yycolumn,yyline,yytext());}

/*Palabra reservada para tipos de temas*/
("dark"|"blue"|"white"|"DARK"|"BLUE"|"WHITE") {return new Symbol(sym.TEMA,yycolumn,yyline,yytext());}

/*Palabra reservada para tipos de temas*/
("CENTRO"|"IZQUIERDA"|"DERECHA"|"JUSTIFICAR") {return new Symbol(sym.ALINEA,yycolumn,yyline,yytext());}

/*Palabra reservada para clases normales*/
("CAMPO_TEXTO"|"FICHERO") {return new Symbol(sym.CLASENORMAL,yycolumn,yyline,yytext());}

/*Palabra reservada para clases area texto*/
("AREA_TEXTO") {return new Symbol(sym.CLASEAREA,yycolumn,yyline,yytext());}

/*Palabra reservada para clases con opciones*/
("CHECKBOX"|"RADIO"|"COMBO") {return new Symbol(sym.CLASEOPCIONES,yycolumn,yyline,yytext());}

/*Palabra reservada para clases de imagen*/
("IMAGEN") {return new Symbol(sym.CLASEIMAGEN,yycolumn,yyline,yytext());}

/*Palabra reservada para clase de boton*/
("BOTON") {return new Symbol(sym.CLASEBOTON,yycolumn,yyline,yytext());}

/*Palabra reservada para tipos de temas*/
("SI"|"NO") {return new Symbol(sym.BOOL,yycolumn,yyline,yytext());}

/*Palabra reservada para eliminar formulario*/
("ELIMINAR_FORMULARIO") {return new Symbol(sym.ELIMINARF,yycolumn,yyline,yytext());}

/*Palabra reservada para modificar formulario*/
("MODIFICAR_FORMULARIO") {return new Symbol(sym.MODIFICARF,yycolumn,yyline,yytext());}

/*Palabra reservada para parametros*/
("PARAMETROS_FORMULARIO") {return new Symbol(sym.PARAMF,yycolumn,yyline,yytext());}

/*Palabra reservada para agregar componente*/
("AGREGAR_COMPONENTE") {return new Symbol(sym.AGREGARC,yycolumn,yyline,yytext());}

/*Palabra reservada para nombre del campo*/
("NOMBRE_CAMPO") {return new Symbol(sym.NOMBREC,yycolumn,yyline,yytext());}

/*Palabra reservada para formulario*/
("FORMULARIO") {return new Symbol(sym.FORMULARIOC,yycolumn,yyline,yytext());}

/*Palabra reservada para clase*/
("CLASE") {return new Symbol(sym.CLASE,yycolumn,yyline,yytext());}

/*Palabra reservada para indice*/
("INDICE") {return new Symbol(sym.INDICEC,yycolumn,yyline,yytext());}

/*Palabra reservada para el texto visible del componente*/
("TEXTO_VISIBLE") {return new Symbol(sym.TEXTOVC,yycolumn,yyline,yytext());}

/*Palabra reservada para alineacion*/
("ALINEACION") {return new Symbol(sym.ALINEAC,yycolumn,yyline,yytext());}

/*Palabra reservada para requerido*/
("REQUERIDO") {return new Symbol(sym.REQUERIDO,yycolumn,yyline,yytext());}

/*Palabra reservada para opciones*/
("OPCIONES") {return new Symbol(sym.OPCIONES,yycolumn,yyline,yytext());}

/*Palabra reservada para filas*/
("FILAS") {return new Symbol(sym.FILAS,yycolumn,yyline,yytext());}

/*Palabra reservada para columnas*/
("COLUMNAS") {return new Symbol(sym.COLUMNAS,yycolumn,yyline,yytext());}

/*Palabra reservada para url*/
("URL") {return new Symbol(sym.URLC,yycolumn,yyline,yytext());}

/*Palabra reservada para eliminar*/
("ELIMINAR_COMPONENTE") {return new Symbol(sym.ELIMINARC,yycolumn,yyline,yytext());}

/*Palabra reservada para parametros de los componentes*/
("PARAMETROS_COMPONENTE") {return new Symbol(sym.PARAMC,yycolumn,yyline,yytext());}

/*Palabra reservada para modificar los componentes*/
("MODIFICAR_COMPONENTE") {return new Symbol(sym.MODIFICARC,yycolumn,yyline,yytext());}

/*Palabra reservada para mandar solicitud para las credenciales del usuario*/
("CREDENCIALES_USUARIO") {return new Symbol(sym.CREDENCIALES,yycolumn,yyline,yytext());}

/* Dos puntos */
( ":" ) {return new Symbol(sym.DOSP, yycolumn, yyline, yytext());}

/* Coma */
( "," ) {return new Symbol(sym.COMA, yycolumn, yyline, yytext());}

/* Parentesis de apertura */
( "(" ) {return new Symbol(sym.Parentesis_a, yycolumn, yyline, yytext());}

/* Parentesis de cierre */
( ")" ) {return new Symbol(sym.Parentesis_c, yycolumn, yyline, yytext());}

/* Llave de apertura */
( "{" ) {return new Symbol(sym.LLAVEA, yycolumn, yyline, yytext());}

/* Llave de cierre */
( "}" ) {return new Symbol(sym.LLAVEC, yycolumn, yyline, yytext());}

/* Corchete de apertura */
( "[" ) {return new Symbol(sym.CORCHETEA, yycolumn, yyline, yytext());}

/* Corchete de cierre */
( "]" ) {return new Symbol(sym.CORCHETEC, yycolumn, yyline, yytext());}

/* Comillas */
( "\"" ) {return new Symbol(sym.COMILLAS, yycolumn, yyline, yytext());}

/* Numero */
{D}+|{D}+"."{D}+ {return new Symbol(sym.NUMERO, yycolumn, yyline, yytext());}

/*Fecha*/
{D}{4}"-"({D}{2}"-"|{D}"-")({D}{2}|{D}) {return new Symbol(sym.FECHA,yycolumn,yyline,yytext());}

/*Usuario token*/
({L}|{D}|{C})+ {return new Symbol(sym.USUARIO,yycolumn,yyline,yytext());}

/* ID */
("$"|"_"|"-")("$"|"_"|"-"|{D}|{L})* {return new Symbol(sym.ID,yycolumn,yyline,yytext());}

/*URL*/
("https://")?{L}({L})*"."{L}({L})*".com/"(({L}|{D}|{C})*("/")?)* {return new Symbol(sym.URL, yycolumn,yyline,yytext());}

/*TITULO*/
({L}|{D})({L}|{D}|{C}|{esp})* {return new Symbol(sym.TITULO, yycolumn, yyline, yytext());}

/* Espacios en blanco */
{espacio} {/*Ignore*/}

/* Comentarios */
( "//"(.)* ) {/*Ignore*/}

