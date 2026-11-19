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

  Scenario: Modificar un nivel
    When creo un nivel con título "Nivel temporal"
    And modifico el nivel creado con título "Nivel editado"
    Then la respuesta tiene estado 200
    And el campo "$.titulo" de la respuesta es "Nivel editado"
    
    When doy de baja el nivel creado
    Then la respuesta es exitosa

  Scenario: Ciclo completo de un ejercicio
    When creo un nivel con título "Nivel con ejercicio"
    And creo un ejercicio "Ejercicio nuevo" en el nivel creado
    Then la respuesta tiene estado 201

    When consulto el ejercicio creado
    Then el campo "$.titulo" de la respuesta es "Ejercicio nuevo"
    
    When consulto el bloque del ejercicio creado
    Then el campo "$.contenido" de la respuesta es "print(1)"
    
    When modifico el bloque del ejercicio creado con contenido "print(2)"
    Then el campo "$.contenido" de la respuesta es "print(2)"
    
    When modifico el ejercicio creado con título "Ejercicio editado"
    Then el campo "$.titulo" de la respuesta es "Ejercicio editado"
    
    When elimino el ejercicio creado
    Then la respuesta tiene estado 204
    
    When doy de baja el nivel creado
    Then la respuesta es exitosa

  Scenario: Cuerpo JSON inválido
    When envío un cuerpo JSON inválido a "/api/admin/niveles"
    Then la respuesta tiene estado 400