package io.stricte.jogging.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "authority")
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String name;
}