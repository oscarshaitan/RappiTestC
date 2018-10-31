Modelos:
Genre: clase que representa los g�neros
GenreList: clase que representa el listado de g�neros, utilziada en la comunicaci�n con Retrofit
TopMovie: clase que representa el listado de pel�culas, utilizada en la comunicaci�n con Retrofit
MyMovie: clase que representa el detalle de una pel�cula
MyTv: clase que representa el detalle de una serie de tv
RetroYoutube: clase que representa el video de YouTube
RetroYoutubeList: clase que representa el listado de los videos de YouTube
TopTv: clase que representa el listado de las series de Tv, utilizada en la comunicaci�n con Retrofit
TvSeriesList: clase que representa el resumen de la serie utilizado en el listado de series
MovieList: clase que representa el resumen de la pel�cula utilizado en el listado de pel�culas

Red:
RetroDataService: Interfaz que contiene los endpoint usados de la API
RetrofitClientInstance: clase de la instancia de Retrofit

Test:
MainGUI: Clase de las pruebas unitarias


Vista:
EndlessRecyclerViewScrollListener: clase que permite el scroll infinito en el recyclerview
MovieAdapter: adaptador para la visualizaci�n de los MovieList en la vista principal
TvAdapter: contra parte del MovieAdapter para las series de TV
YoutubeVideoAdapter: adaptador para la visualizaci�n de los videos de YouTube en el detalle

Actividades:
MainActivity: clase de actividad principal donde se representa las pel�culas
MovieDetail: clase de actividad donde se representa el detalle de la pel�cula
TvActivity: contraparte del MainActivity donde se representa las series
TvDetail: contra parte del MovieDetail donde se representan las series de TV


Aunque muchas clases comparten un c�digo muy similar se decidi� mantenerlas separadas para mantener las clases de manera sencilla y de f�cil actualizaci�n, en caso de ser necesario mostrar m�s detalles diferenciadores se pueden realizar de mejor manera.

Responda y escriba dentro del Readme con las siguientes preguntas:

1. En qu� consiste el principio de responsabilidad �nica? �Cu�l es su prop�sito?
Las clases o m�todos cumplan una �nica parte de la funcionalidad completa, esto sirve para evitar el acoplamiento de las funciones y as� tener mejor mantenimiento de la aplicaci�n

2. �Qu� caracter�sticas tiene, seg�n su opini�n, un �buen� c�digo o c�digo limpio?

Considero un buen c�digo un c�digo que sus funciones realicen una funci�n, esto hace que la clase sea m�s larga, pero identificar el comportamiento de cada funci�n es m�s f�cil y adicional es m�s f�cil hacer un debug dado un caso de encontrar suceder un bug.
Funciones con un nombre descriptivo, si el nombre es lo suficientemente descriptivo es m�s f�cil entender cu�l es la funci�n de este.








