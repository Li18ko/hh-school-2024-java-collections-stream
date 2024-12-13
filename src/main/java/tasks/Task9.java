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
    return persons.subList(1, persons.size()).stream().map(Person::firstName).collect(Collectors.toList());
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
    Map<Integer, String> map = persons.stream().collect(Collectors.toMap(
            //игнорирую дубликаты
            Person::id, this::convertPersonToString, (existing, replacement) -> existing));
    return map;
  }

  // есть ли совпадающие в двух коллекциях персоны?
  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    //Вместо for в for использую Stream Api для проверки есть хотя бы один совпадающий элемент
    return persons1.stream().anyMatch(person1 -> persons2.stream().anyMatch(persons1::equals));
  }

  // Посчитать число четных чисел
  public long countEven(Stream<Integer> numbers) {
    //Использую сразу метод count вместо forEach
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
    assert snapshot.toString().equals(set.toString());
  }
}
