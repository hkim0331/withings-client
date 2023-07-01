# Withings-Client

withings token manager.

🔥UNDER CONSTRUCTION🔥

generated using Luminus version "4.40"

    % lein new luminus withings-client +site +reagent +mysql

## Prerequisites

* [Leiningen][1] 2.0 or above installed.
* mariadb 10.3 or mysql
* npm (maybe nodejs)

[1]: https://github.com/technomancy/leiningen

## Run (develop, need DevContainer)

not MariaDB 11. use 10.3.


or in VScode,


## Run kohhoh

Use /home/ubuntu/wc/{start,stop,restart}.sh scripts.

## Usage

  % wc-login.sh user password
  % wc-lastupdate.sh | wc-date-value.clj

## bin/

- wc-login.sh _login_ _password_ -- to start a session.
  must be executed before before any of operations bellow.
- wc-users.sh -- get all users
- wc-toggle-valid.sh _id_ -- toggle users validity
- wc-lastupdate.sh _id_ _date_ -- fetch user id's last updated data.
- wc-start-end.sh _id_ _startdate_ _enddate_ -- fetch user id's data between startdate and enddate.
- wc-refresh-all-auto.sh
- wc-refresh-all.sh -- refresh all existent tokens
- wc-refresh.sh _id_ -- refresh user id's token

## License

Copyright © 2022 Hiroshi Kimura
