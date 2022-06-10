package com.devsuperior.dscatalog.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "tb_category")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;

	// Atributo usado para documentar o momento exato da criação desta
	// entidade, será armazenado em UTC ou seja deveremos formatá-lo
	// em sua exibição subtraindo 3 horas
	@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
	private Instant createdAt;

	@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
	private Instant updatedAt;

	// Como o mapeamento principal esta definido na classe Product
	// usamos o mappedBy para mapear a outra ponta e por isso
	// usamos o nome do atributo do mapeamento da classe Product o categories
	@ManyToMany(mappedBy = "categories" )
	private Set<Product> products = new HashSet<>();
	
	public Category() {
	}

	public Category(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	// Método que antes de salvar no banco grava o dado
	// na variável referente a data da criação da entidade
	@PrePersist
	public void prePersist() {
		createdAt = Instant.now();
	}

	// Método que antes de realizar o upDate no banco grava o dado
	// na variável referente a data da atualização da entidade
	@PreUpdate
	public void preUpdate() {
		updatedAt = Instant.now();
	}

	
	public Set<Product> getProducts() {
		return products;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return Objects.equals(id, other.id);
	}

}
