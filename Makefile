DEST=ubuntu@kohhoh.jp

all: clean deploy

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

target/uberjar/withings-client.jar:
	lein uberjar

deploy: target/uberjar/withings-client.jar
	scp target/uberjar/withings-client.jar ${DEST}:wc/withings-client.jar && \
	ssh ${DEST} 'sudo systemctl restart withings-client' && \
	ssh ${DEST} 'systemctl status withings-client'

clean:
	${RM} -r target
