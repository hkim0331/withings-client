DEST=ubuntu@kohhoh.jp

rep:
	npm install
	npm install xmlhttprequest

mysql: mariadb

mariadb:
	docker run --detach --name mariadb -p 3306:3306 \
		--env MARIADB_USER=user --env MARIADB_PASSWORD=secret \
		--env MARIADB_ROOT_PASSWORD=admin  mariadb:latest

uberjar:
	lein uberjar

target/default+uberjar/withings-client.jar:
	lein uberjar

deploy: target/default+uberjar/withings-client.jar
	scp target/default+uberjar/withings-client.jar ${DEST}:wc/withings-client.jar && \
	ssh ${DEST} 'sudo systemctl restart wc' && \
	ssh ${DEST} 'systemctl status wc'

clean:
	${RM} target/default+uberjar/withings-client.jar
