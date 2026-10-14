/*
    Es necesario asignar el role_specification para el esquema.

    Donde el role_specification puede ser:
    
    user_name
  | CURRENT_ROLE
  | CURRENT_USER
  | SESSION_USER

  y este será el propietario del esquema
*/

CREATE SCHEMA redeco AUTHORIZATION postgres;
