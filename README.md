# ParcialAREP1T

## author

Maria Paula Sánchez Macías

## Ejecución de código

- clonar el repo

git clone https://github.com/hakki17/ParcialAREP1T

- ejecutar en el IDE  de preferencia
para esto hay que ejecutar ambas clases 

- ingresar a http://localhost:35000/calculadora

## descripción

el parcial constaba de construir una "Calculadora Web para estimar la media y la desviación estándar de un conjunto de números ". La solución consta de un servidor backend que responde a solicitudes HTTP GET de la Facade, un servidor Facade que responde a solicitudes HTTP GET del cliente , y un cliente Html+JS que envía los comandos y muestra las respuestas.

Profe no supe hacerlo dividiendo las funciones, asi que hice en el backend la clase que calculaba tanto la desviaión como la media. Para la media, era una funcion sencilla que cogía la lista de parametros (quitandoles la separacion por ",") y sumandola para luego dividirla entre el número n de números que habían

Para la desviación hacemos la ecuación usando métodos Math como pow (^2), y sqrt (raiz cuadrada)

## directorio

![](https://github.com/hakki17/ParcialAREP1T/blob/main/img/arquitectura.png)
