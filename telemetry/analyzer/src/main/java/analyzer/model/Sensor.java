package analyzer.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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

//    @OneToMany
//    @MapKeyColumn(
//            table = "scenario_actions",
//            name = "sensor_id")
//    @JoinTable(
//            name = "scenario_actions",
//            joinColumns = @JoinColumn(name = "scenario_id"),
//            inverseJoinColumns = @JoinColumn(name = "action_id"))
//    private Map<String, Action> actions = new HashMap<>();
//
//    @OneToMany
//    @MapKeyColumn(
//            table = "scenario_conditions",
//            name = "sensor_id")
//    @JoinTable(
//            name = "scenario_conditions",
//            joinColumns = @JoinColumn(name = "scenario_id"),
//            inverseJoinColumns = @JoinColumn(name = "condition_id"))
//    private Map<String, Condition> conditions = new HashMap<>();
}