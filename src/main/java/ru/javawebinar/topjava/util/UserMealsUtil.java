package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
//         TODO Implement by streams
        Map<LocalDate, Integer> dateAndCaloriesPerDay = new HashMap<>();
        return meals.stream()
                .peek(x->dateAndCaloriesPerDay.merge(x.getDateTime().toLocalDate(),x.getCalories(),Integer::sum)).collect(Collectors.toList()).stream()
                .sorted(Comparator.comparing(UserMeal::getDateTime))
                .filter(x->TimeUtil.isBetweenHalfOpen(x.getDateTime().toLocalTime(),startTime,endTime))
                .map(x -> dateAndCaloriesPerDay.get(x.getDateTime().toLocalDate())>caloriesPerDay ? new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(), true) :
                        new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(), false))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
//         TODO return filtered list with excess. Implement by cycles
        Map<LocalDate, Integer> dateAndCaloriesPerDay = new HashMap<>();
        List<UserMeal> userMealsWithRightDate = new ArrayList<>();
        List<UserMealWithExcess> result = new ArrayList<>();

        for (UserMeal um : meals) {
            dateAndCaloriesPerDay.merge(um.getDateTime().toLocalDate(), um.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime)) {
                userMealsWithRightDate.add(um);
            }
        }

        for (UserMeal um : userMealsWithRightDate) {
            if (dateAndCaloriesPerDay.get(um.getDateTime().toLocalDate()) > caloriesPerDay) {
                result.add(new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(), true));
            } else {
                result.add(new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(), false));
            }
        }

        result.sort((um1, um2) -> {
            if (um1.getDateTime().isEqual(um2.getDateTime())) {
                return 0;
            } else if (um1.getDateTime().isBefore(um2.getDateTime())) {
                return -1;
            } else {
                return 1;
            }
        });
        return result;
    }
}
