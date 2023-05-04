Create database table for Games


# Acceptance Criteria:

- [ ] There is an `@Entity` class called Game.java
- [ ] There is a `@Repository` class called Game.java
- [ ] When you start up the repo on localhost, you can see the table
      using the H2 console (see the file `docs/h2-database.md` for 
      instructions.)
- [ ] You can see the games table when you do these steps:
      1. Connect to postgres command line with 
         ```
         dokku postgres:connect team02-qa-db
         ```
      2. Enter `\dt` at the prompt. You should see
         `games` listed in the table.
      3. Use `\q` to quit


