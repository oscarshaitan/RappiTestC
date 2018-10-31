Modelos:
Genre: clase que representa los géneros
GenreList: clase que representa el listado de géneros, utilziada en la comunicación con Retrofit
TopMovie: clase que representa el listado de películas, utilizada en la comunicación con Retrofit
MyMovie: clase que representa el detalle de una película
MyTv: clase que representa el detalle de una serie de tv
RetroYoutube: clase que representa el video de YouTube
RetroYoutubeList: clase que representa el listado de los videos de YouTube
TopTv: clase que representa el listado de las series de Tv, utilizada en la comunicación con Retrofit
TvSeriesList: clase que representa el resumen de la serie utilizado en el listado de series
MovieList: clase que representa el resumen de la película utilizado en el listado de películas

Red:
RetroDataService: Interfaz que contiene los endpoint usados de la API
RetrofitClientInstance: clase de la instancia de Retrofit

Test:
MainGUI: Clase de las pruebas unitarias


Vista:
EndlessRecyclerViewScrollListener: clase que permite el scroll infinito en el recyclerview
MovieAdapter: adaptador para la visualización de los MovieList en la vista principal
TvAdapter: contra parte del MovieAdapter para las series de TV
YoutubeVideoAdapter: adaptador para la visualización de los videos de YouTube en el detalle

Actividades:
MainActivity: clase de actividad principal donde se representa las películas
MovieDetail: clase de actividad donde se representa el detalle de la película
TvActivity: contraparte del MainActivity donde se representa las series
TvDetail: contra parte del MovieDetail donde se representan las series de TV


Aunque muchas clases comparten un código muy similar se decidió mantenerlas separadas para mantener las clases de manera sencilla y de fácil actualización, en caso de ser necesario mostrar más detalles diferenciadores se pueden realizar de mejor manera.

Responda y escriba dentro del Readme con las siguientes preguntas:

1. En qué consiste el principio de responsabilidad única? ¿Cuál es su propósito?
Las clases o métodos cumplan una única parte de la funcionalidad completa, esto sirve para evitar el acoplamiento de las funciones y así tener mejor mantenimiento de la aplicación

2. ¿Qué características tiene, según su opinión, un “buen” código o código limpio?

Considero un buen código un código que sus funciones realicen una función, esto hace que la clase sea más larga, pero identificar el comportamiento de cada función es más fácil y adicional es más fácil hacer un debug dado un caso de encontrar suceder un bug.
Funciones con un nombre descriptivo, si el nombre es lo suficientemente descriptivo es más fácil entender cuál es la función de este.








