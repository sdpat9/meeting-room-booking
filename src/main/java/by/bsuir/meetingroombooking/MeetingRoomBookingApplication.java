package by.bsuir.meetingroombooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MeetingRoomBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetingRoomBookingApplication.class, args);
	}

}
