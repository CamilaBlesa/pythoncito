Feature: Administración de niveles

  Background:
    Given un administrador autenticado

  Scenario: El administrador lista todos los niveles
    When hago un GET a "/api/admin/niveles"
    Then la respuesta tiene estado 200
    And la respuesta tiene al menos 5 elementos

  Scenario: Crear un nivel y darlo de baja
    When creo un nivel con título "Nivel de prueba"
    Then la respuesta es exitosa
    And el campo "$.titulo" de la respuesta es "Nivel de prueba"
    When doy de baja el nivel creado
    Then la respuesta es exitosa
    And el campo "$.activo" de la respuesta es "false"

  Scenario: Ejercicio inexistente en administración
    When hago un GET a "/api/admin/ejercicios/999999"
    Then la respuesta tiene estado 404