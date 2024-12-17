package tasks;

import common.Person;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
Далее вы увидите код, который специально написан максимально плохо.
Постарайтесь без ругани привести его в надлежащий вид
P.S. Код в целом рабочий (не везде), комментарии оставлены чтобы вам проще понять чего же хотел автор
P.P.S Здесь ваши правки необходимо прокомментировать (можно в коде, можно в PR на Github)
 */
public class Task9 {

  private long count;

  // Костыль, эластик всегда выдает в топе "фальшивую персону".
  // Конвертируем начиная со второй
  public List<String> getNames(List<Person> persons) {
    //заменю persons.size() == 0 на persons.isEmpty(), persons.isEmpty() быстрее проверяет List
    if (persons.isEmpty()) {
      return Collections.emptyList();
    }

    //вместо удаления нулевого элемента, взяла верезку List с помощью subList (так быстрее)
    //persons.remove(0);  -- List.remove(0) -- алгоритмическая сложность O(n),
    //так как при удалении элемента, все оставшиеся элементы справа сдвинутся влево
    //big O = O(n)

    //persons.subList(1, persons.size())  -- List.subList(begin, end) -- алгоритмическая сложность O(m),
    //где m = end-begin, так как поиск элемента по индексу O(1)
    //Алгоритмическая сложность будет меньше, чем у remove, но Big O будут равны,
    //так как можно прировнять m с n
    //O(m) = O(n), big O = O(n)


    //Пересмотрела промежуточные операторы в лекции, можно использовать skip(long n),
    //чтобы не создавать новый лист, а потом снова stream
    //Преимущество использования skip перед remove заключается в том, что мы не меняем список,
    //а просто пропускаем какое-то количество элементов (первоначальный вид списка не нарушается)
    //Это дает плюс в том, что уменьшается возможность проблемы из-за многократной обработки листа
    return persons.stream()
            .skip(1)
            .map(Person::firstName)
            .collect(Collectors.toList());
  }

  // Зачем-то нужны различные имена этих же персон (без учета фальшивой разумеется)
  public Set<String> getDifferentNames(List<Person> persons) {
    //Создание HashSet<>, который поддерживает уникальность, вместо использования distinct
    return new HashSet<>(getNames(persons));
  }

  // Тут фронтовая логика, делаем за них работу - склеиваем ФИО
  public String convertPersonToString(Person person) {
      // Убрала склейку строк через кучу if, вместо это использовала joing с фильтрацией != null
      return Stream.of(person.secondName(), person.firstName(), person.middleName())
            .filter(Objects::nonNull).collect(Collectors.joining(" "));
  }

  // словарь id персоны -> ее имя
  public Map<Integer, String> getPersonNames(Collection<Person> persons) {
    //вместо for использую более краткую запись stream api
    return persons.stream().collect(Collectors.toMap(
            //игнорирую дубликаты
            Person::id, this::convertPersonToString, (existing, replacement) -> existing));
  }

  // есть ли совпадающие в двух коллекциях персоны?
  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    //Вместо for в for использую Stream Api для проверки есть хотя бы один совпадающий элемент
    //Алгоритмическая сложность O(n*m), где n - длина persons1 (проходит по каждому элементу)
    //m - длина persons2 (проходит по каждому элементу)
    //Мы для каждого persons1 будет проходить по всем persons2
    //return persons1.stream().anyMatch(person1 -> persons2.stream().anyMatch(persons1::equals));

    //Можно без использования второго стрима, через contains
    //По скорости будет зависеть от вида коллекции
    //List будет также O(n*m)
    //HashSet будет O(n), так как проверка для каждого элемента persons1 будет выполняться за O(1)
    //return persons1.stream().anyMatch(persons2::contains);

    //Можно гарантировано быстрее
    //Так как могут быть разные коллекции, сразу преобразуем в HashSet,
    //так как у HashSet меньше алгоритмическая сложность
    Set<Person> personHashSet = new HashSet<>(persons2);
    return persons1.stream().anyMatch(personHashSet::contains);

  }

  // Посчитать число четных чисел
  public long countEven(Stream<Integer> numbers) {
    //Использую сразу метод count вместо forEach
    //forEach влияет на внешнюю переменную count
    //В функциональном программировании нельзя изменять внешнее состояние
    //Перемення count находится за пределами stream и видоизменяется внутри него,
    //что противоречит функциональному программированию
    return numbers.filter(num -> num % 2 == 0).count();
  }

  // Загадка - объясните почему assert тут всегда верен
  // Пояснение в чем соль - мы перетасовали числа, обернули в HashSet, а toString() у него вернул их в сортированном порядке
  void listVsSet() {
    List<Integer> integers = IntStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList());
    List<Integer> snapshot = new ArrayList<>(integers);
    Collections.shuffle(integers);
    Set<Integer> set = new HashSet<>(integers);
    //В HashSet<> toString() всегда возвращает отсортированный вариант
    //Я имела ввиду, что отсортированный по хэш-коду

    //List snapshot содержит элементы от 1 до 10000, порядок будет соответствовать
    //порядку элементов в листе

    //В HashSet set передается перемешанный порядок от 1 до 10000
    //(перемешанный из-за Collections.shuffle(integers);)
    //Но так как у нас Integer и хэш-код будет равен значению,
    //то у нас элементы отсортируются также от 1 до 10000 по хэш-коду

    //А toString вернет HashSet отсортировано по хэш-коду
    assert snapshot.toString().equals(set.toString());
  }
}
