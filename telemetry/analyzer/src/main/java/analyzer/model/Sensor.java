package analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sensors")
public class Sensor {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;
}