Feature: Recorrido del estudiante por niveles y ejercicios

  Background:
    Given un estudiante autenticado

  Scenario: Un estudiante nuevo solo tiene desbloqueado el primer nivel
    Then el nivel 1 está desbloqueado
    And el nivel 2 está bloqueado

  Scenario: No se puede abrir un nivel bloqueado
    When intento abrir el nivel 2
    Then la respuesta tiene estado 403

  Scenario: Se pueden ver los ejercicios del primer nivel
    When consulto los ejercicios del nivel 1
    Then la respuesta tiene estado 200
    And la respuesta tiene 1 elemento

  Scenario: Ejercicio inexistente
    When hago un GET a "/api/estudiante/ejercicios/999999"
    Then la respuesta tiene estado 404

  Scenario: Solución correcta
    When resuelvo correctamente el ejercicio del nivel 1
    Then la respuesta tiene estado 200
    And el campo "$.correcta" de la respuesta es "true"
    And el campo "$.mensaje" de la respuesta es "Excelente, la solucion es correcta."

  Scenario: Bloques en orden incorrecto
    When envío los bloques del ejercicio del nivel 1 en orden inverso
    Then el campo "$.correcta" de la respuesta es "false"
    And el campo "$.mensaje" de la respuesta es "El orden de los bloques no es correcto. Revisa la secuencia logica."

  Scenario: Faltan bloques
    When envío los bloques del ejercicio del nivel 1 sin el último bloque
    Then el campo "$.correcta" de la respuesta es "false"
    And el campo "$.mensaje" de la respuesta es "Faltan bloques para completar la solucion."

  Scenario: Se envía un bloque que no pertenece al ejercicio
    When envío los bloques del ejercicio del nivel 1 con un bloque ajeno
    Then el campo "$.correcta" de la respuesta es "false"
    And el campo "$.mensaje" de la respuesta es "Hay bloques que no pertenecen a este ejercicio."

  Scenario: Completar un nivel desbloquea el siguiente y queda en el progreso
    When resuelvo correctamente el ejercicio del nivel 1
    Then el nivel 2 está desbloqueado
    When consulto mi progreso
    Then la respuesta tiene 1 elemento
    And el campo "$[0].titulo" de la respuesta es "Nivel 1: Variables y Mensajes"

  Scenario: Los intentos quedan registrados en orden
    When envío los bloques del ejercicio del nivel 1 en orden inverso
    And resuelvo correctamente el ejercicio del nivel 1
    And consulto los intentos del ejercicio del nivel 1
    Then la respuesta tiene 2 elementos
    And el campo "$[0].numeroIntento" de la respuesta es "1"
    And el campo "$[0].correcta" de la respuesta es "false"
    And el campo "$[1].numeroIntento" de la respuesta es "2"
    And el campo "$[1].correcta" de la respuesta es "true"

  Scenario: La primera pista está disponible desde el inicio
    When pido la pista 1 del ejercicio del nivel 1
    Then la respuesta tiene estado 200
    And el campo "$.orden" de la respuesta es "1"

  Scenario: La segunda pista requiere un intento fallido
    When pido la pista 2 del ejercicio del nivel 1
    Then la respuesta tiene estado 403

  Scenario: La segunda pista se desbloquea tras un intento fallido
    When envío los bloques del ejercicio del nivel 1 en orden inverso
    And pido la pista 2 del ejercicio del nivel 1
    Then la respuesta tiene estado 200
    And el campo "$.orden" de la respuesta es "2"

  Scenario Outline: Pistas inválidas o inexistentes
    When pido la pista <orden> del ejercicio del nivel 1
    Then la respuesta tiene estado <estado>

    Examples:
      | orden | estado |
      | 0     | 400    |
      | 99    | 404    |

  Scenario: Se puede abrir el primer nivel
    When intento abrir el nivel 1
    Then la respuesta tiene estado 200
    And el campo "$.titulo" de la respuesta es "Nivel 1: Variables y Mensajes"

  Scenario: Se puede ver el detalle de un ejercicio
    When consulto el detalle del ejercicio del nivel 1
    Then la respuesta tiene estado 200
    And el campo "$.titulo" de la respuesta es "Mi primer saludo"

  Scenario: Validación directa con el cuerpo completo
    When valido directamente la solución correcta del ejercicio del nivel 1
    Then el campo "$.correcta" de la respuesta es "true"