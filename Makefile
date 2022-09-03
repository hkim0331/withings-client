rep:
	npm install
	npm install xmlhttprequest

mysql: mariadb

mariadb:
	docker run --detach --name mariadb -p 3306:3306 \
		--env MARIADB_USER=user --env MARIADB_PASSWORD=secret \
		--env MARIADB_ROOT_PASSWORD=admin  mariadb:latest
