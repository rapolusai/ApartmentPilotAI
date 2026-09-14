package com.rapolus.apartmentpilotai.api;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.*;
public final class Requests {
    private Requests() {
    }
    public record Login(@Pattern(regexp="[6-9][0-9]{9}") @NotNull String mobile, @Pattern(regexp="[0-9]{4,6}") @NotNull String pin,@Pattern(regexp="ADMIN|TREASURER|RESIDENT") @NotNull String role) {
    }
    public record Register(@NotBlank @Size(max=80) String name,@Pattern(regexp="[6-9][0-9]{9}") @NotNull String mobile, @Pattern(regexp="[0-9]{4,6}") @NotNull String pin,@NotBlank @Size(max=80) String apartmentName, @NotBlank @Size(max=80) String city,@NotNull @Min(5) @Max(50) Integer flats) {
    }
    public record Join(@NotBlank @Size(max=60) String invite,@NotBlank @Size(max=20) String flatLabel, @NotBlank @Size(max=80) String name,@Pattern(regexp="[6-9][0-9]{9}") @NotNull String mobile, @Pattern(regexp="[0-9]{4,6}") @NotNull String pin) {
    }
    public record Rule(@NotNull BigDecimal amount,@Pattern(regexp="[0-9]{4}-[0-9]{2}") @NotNull String effective, @Min(1) @Max(28) int billingDay,@Min(1) @Max(28) int dueDay) {
    }
    public record Payment(@NotNull UUID billId,@NotNull BigDecimal amount, @Pattern(regexp="UPI|CASH|BANK_TRANSFER|CHEQUE") @NotNull String mode, @Size(max=80) String reference,@NotNull LocalDate paidOn,@Size(max=300) String note,@NotNull UUID requestKey) {
    }
    public record Review(@NotEmpty @Size(max=100) List<@NotNull UUID> paymentIds,boolean verified) {
    }
    public record Reminder(@NotNull UUID requestKey) {
    }
    public record Reject(@NotBlank @Size(max=300) String reason) {
    }
    public record Expense(@NotBlank @Size(max=100) String title,@NotBlank @Size(max=30) String category, @NotNull BigDecimal amount,@NotNull LocalDate paidOn,boolean paid,boolean visibleToResidents,@NotNull UUID requestKey) {
    }
    public record Notice(@NotBlank @Size(max=100) String title,@NotBlank @Size(max=2000) String body,boolean publish) {
    }
}
