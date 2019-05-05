package com.revtest.helloworld.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBean {
	@NotNull
	@Size(min = 1, max = 60)
	private String userName;
	@Size(max = 60)
	@NotNull(message = "{user.dateOfBirth.")
	@Past(message = "{user.dateOfBirth.past}")
	private LocalDate dateOfBirth;
	@NotNull
	@PositiveOrZero
	private Integer siblings;
}
