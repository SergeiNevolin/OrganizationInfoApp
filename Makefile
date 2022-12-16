
all:
	@echo "make postgres	- Start postgres container"
	@exit 0

postgres:
	docker stop store-postgres || true
	docker run --detach --name=postgres --env POSTGRES_USER=user --env POSTGRES_PASSWORD=hackme --publish 5432:5432 postgres
