package com.greendata.OrganizationInfo.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EgrulTask {

    @Scheduled(cron = "@daily")
	public void updateActualEgrul() {
		// функция будет запускаться каждый день, делать запрос к сервису https://egrul.itsoft.ru
        // и добавлять актуальную информацию в базе
	}
}