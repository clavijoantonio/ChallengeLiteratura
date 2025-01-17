package com.clavijoAntonio.challangeLiteralura.principal;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.clavijoAntonio.challangeLiteralura.model.*;

import com.clavijoAntonio.challangeLiteralura.repository.IAuthorRepository;
import com.clavijoAntonio.challangeLiteralura.repository.ILibroRepository;
import com.clavijoAntonio.challangeLiteralura.service.ConsumoApi;
import com.clavijoAntonio.challangeLiteralura.service.ConvierteDatos;
import com.clavijoAntonio.challangeLiteralura.service.LibrosService;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi= new ConsumoApi();
    private final String URL_BASE="https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibros> dlibro;
    private ILibroRepository repository;
    private IAuthorRepository autorRepository;
    private List<Libros> libroRegistrado;
    private LibrosService librosService;
    public Principal(ILibroRepository repository, LibrosService librosService, IAuthorRepository  autorRepository){

        this.repository=repository;
        this.librosService=librosService;
        this.autorRepository= autorRepository;
    }

    public void menu(){
        int opcion = -1;

        while(opcion!=0){
            var menu= """
                    1. Busca libro por titulo
                    2. Lista libros registrados
                    3. Lista autores registrados
                    4. Lista autores vivos en un  determinado año
                    5. Lista libros por idioma
                    0. salir
                    """;

            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion){
                case 1:
                    buscarLibrosPorTitulo();
                    break;
                    case 2:
                        listarLibrosRegistados();
                        break;
                        case 3:
                            listarAutoresRegistados();
                        break;
                case 4:
                    listarAutorPorAño();
                    break;
                case 5:
                    listarIdioma();
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }
        }
    }
    public List<DatoApi>  getLibrosPorTitulo() {

        System.out.println("Ingresa el libro que desea buscar: ");
        String titulo = teclado.nextLine();
        List <DatoApi> datos= convierteJsonADatos(titulo);
        return datos;

    }

public void buscarLibrosPorTitulo() {

    DatosLibros li = null;

    List <DatoApi> api = getLibrosPorTitulo();

    api.forEach(System.out::println);
    Optional<DatoApi> lib= api.stream()
            .findFirst();

    if (lib.isPresent()) {
      var libros= lib.get();

        dlibro = api.stream()
                .flatMap(a -> a.results().stream())
                .collect(Collectors.toList());


        List<Libros> libro = dlibro.stream()
                .map(dl -> new Libros(dl))
                .collect(Collectors.toList());

    }

    for (DatosLibros libros : dlibro) {
        li = libros;
        System.out.println(libros);
    }

    Libros libros= new Libros(li);
    repository.save(libros);

  }

  public void listarLibrosRegistados(){

        libroRegistrado = repository.findAll();
        libroRegistrado.forEach(System.out::println);

  }
public void listarAutoresRegistados(){
    registarAuthores();
    List<Personas> autor;
    autor = autorRepository.findAll();
    autor.forEach(System.out::println);
}
  public void registarAuthores(){

        listarLibrosRegistados();

      System.out.println("Elige un libro del cual desea conocer el autor");
        var titulo = teclado.nextLine();
   Optional<Libros> libroEncontrado= libroRegistrado.stream()
           .filter(lr->lr.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
           .findFirst();
      if (libroEncontrado.isPresent()){
          var libro= libroEncontrado.get();
         List <DatoApi> datosApi= convierteJsonADatos(titulo);

          dlibro = datosApi.stream()
                  .flatMap(a -> a.results().stream())
                  .collect(Collectors.toList());
          Optional<DatosPersona>  person = dlibro.stream()
                  .flatMap(p -> p.autores().stream())
                  .findFirst();

          List<Personas> persna = person.stream()
                  .map(dl -> new Personas(dl))
                  .collect(Collectors.toList());
          libro.setAutores(persna);
          persna.forEach(System.out::println);
          repository.save(libro);

      }else{
          System.out.println("libro no encontrado");
      }
  }
public void listarIdioma() {

    System.out.println("Ingresa el idioma para conecer los libros en este lenguaje: ");
    var idioma = teclado.nextLine();
    List<Libros> listaLibros = librosService.findLibroByIdioma(idioma);
    if(listaLibros.isEmpty()) {
        System.out.println("no se encuentra libros para este idioma");
    }else{
        listaLibros.forEach(System.out::println);
    }
}
   public void listarAutorPorAño(){
       System.out.println("Ingresa Año para cual desea concer los autores vivos para esta epoca: ");
       var año = teclado.nextLine();
       List<Personas> autores = autorRepository.findAllbyAño(año);
       if(autores.isEmpty()){
           System.out.println("No hay autores vivos para este Año especifico");
       }else {
           autores.forEach(System.out::println);
       }
   }
    public List<DatoApi> convierteJsonADatos(String tituloLibros){

        var json = consumoApi.obtenerJson(URL_BASE+tituloLibros.replace(" ","+"));
        DatoApi datos= conversor.obtenerDatos(json, DatoApi.class);
        List<DatoApi> datosApi= new ArrayList<>();
        datosApi.add(datos);
        datosApi.forEach(System.out::println);
        return datosApi;
    }

}