package hello.springmvc.basic.config.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CronTable {
    private final DeviceService deviceService;

    @Value("${schedule.use}")
    private boolean useSchedule;

    @Scheduled(cron = "${schedule.cron}")
    public void mainJob() {
        try {
            if (useSchedule) {
                deviceService.synchronize();
            }
/*        } catch (InterruptedException e) {
            log.info("* Thread가 강제 종료되었습니다. Message: {}", e.getMessage());*/
        } catch (Exception e) {
            log.info("* Batch 시스템이 예기치 않게 종료되었습니다. Message: {}", e.getMessage());
        }
    }
}