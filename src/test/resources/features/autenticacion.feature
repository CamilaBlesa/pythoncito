Feature: Autenticación y control de acceso

  Scenario: Un estudiante nuevo recibe un token
    When ingreso como estudiante nuevo
    Then la respuesta tiene estado 200
    And la respuesta contiene un token

  Scenario: El administrador ingresa con la clave correcta
    When ingreso como administrador con la clave correcta
    Then la respuesta tiene estado 200
    And la respuesta contiene un token

  Scenario: Clave de administrador incorrecta
    When ingreso como administrador con la clave "clave-mala"
    Then la respuesta tiene estado 401

  Scenario: Un estudiante recupera su sesión con su token
    Given un estudiante autenticado
    When recupero la sesión con mi token actual
    Then la respuesta tiene estado 200
    And la respuesta contiene un token

  Scenario: Un administrador no puede recuperar sesión de estudiante
    Given un administrador autenticado
    When recupero la sesión con mi token actual
    Then la respuesta tiene estado 401

  Scenario Outline: Acceso a rutas protegidas según el rol
    Given <rol>
    When hago un GET a "<ruta>"
    Then la respuesta tiene estado <estado>

    Examples:
      | rol                          | ruta                    | estado |
      | un visitante sin autenticar  | /api/estudiante/niveles | 401    |
      | un visitante sin autenticar  | /api/admin/niveles      | 401    |
      | un estudiante autenticado    | /api/admin/niveles      | 403    |
      | un administrador autenticado | /api/estudiante/niveles | 403    |
      | un estudiante autenticado    | /api/estudiante/niveles | 200    |