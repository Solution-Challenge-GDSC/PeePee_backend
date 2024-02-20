package com.gdsc.solutionchallenge.meetup.entity;


import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @Column(nullable = false, name = "LOCATION", columnDefinition = "GEOMETRY")
    private Point location;

    @OneToOne
    @JoinColumn(name = "meet_id")
    private Meetup meetup;

    public void addMeetup(Meetup meetup){
        this.meetup =meetup;
    }
}