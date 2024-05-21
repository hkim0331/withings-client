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

timer-enable:
	cp withings-timer.* /lib/systemd/system
	systemctl daemon-reload
	systemctl start withings-refresh.timer
	systemctl enable withings-refresh.timer

refresh:
	scp withings-refresh.service withings-refresh.timer  ${DEST}:withings-client
	ssh ${DEST} 'sudo cp withing-client/withings-refresh.* /lib/systemd/system'
	ssh ${DEST} 'sudo systemctl daemon-refresh'
	ssh ${DEST} 'sudo systemctl restart withings-refresh.timer' && \
	ssh ${DEST} 'systemctl status withings-refresh'
