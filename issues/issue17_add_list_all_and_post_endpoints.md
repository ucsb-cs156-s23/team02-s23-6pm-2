Create `DogController`, add `GET` (index) and `POST` (create)

# Acceptance Criteria:

- [ ] There is a controller file `DogController.java`
      in the expected directory.
- [ ] In `DogController.java` there is 
      code for a `GET /api/dogs/all` endpoint 
      that returns a JSON list of all `Dog`s in the database.
      (We sometimes call this an *index* action since it lists all
      items in the database.)
- [ ] In `DogController.java` there is 
      code for a `POST /api/dogs/post` endpoint
      that can be used to create a new entry in the table. (This
      is a *create* action.)
- [ ] The Swagger-UI endpoints for these are well documented so that
      any member of the team can understand what they are for and
      how to use them.
- [ ] The `POST` endpoint works as expected, in the sense that new
      records can be added to the database (on localhost).
- [ ] The `GET` endpoint works as expected, in the sense that the new
      records that are added show up (on localhost).
- [ ] The `GET` and `POST` endpoints work as expected when the 
      app is deployed to Dokku.
- [ ] There is full test coverage (Jacoco) for the methods in 
      `DogController.java`
- [ ] There is full mutation test coverage (Pitest) for the methods in
      `DogController.java`



