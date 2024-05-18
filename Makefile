TAG=hkim0331/luminus:0.1
DEST=ubuntu@kohhoh.jp

all: clean deploy

prep:
	npm install
	npm install xmlhttprequest

mysql: mariadb

mariadb:
	docker run --detach --name mariadb -p 3306:3306 \
		--env MARIADB_USER=user \
		--env MARIADB_PASSWORD=secret \
		--env MARIADB_ROOT_PASSWORD=admin \
		mariadb:latest

uberjar:
	lein uberjar

deploy: uberjar
	scp target/uberjar/withings-client.jar ${DEST}:withings-client/withings-client.jar && \
	ssh ${DEST} 'sudo systemctl restart withings-client' && \
	ssh ${DEST} 'systemctl status withings-client'

clean:
	${RM} -r target

build:
	docker build -t $TAG .
