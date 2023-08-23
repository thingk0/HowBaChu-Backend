package com.HowBaChu.howbachu.domain.entity;

import com.HowBaChu.howbachu.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Opin extends BaseEntity {

    @Id
    @Column(name = "opin_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Opin parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Opin> children = new ArrayList<>();

    @Column(nullable = false, length = 300)
    private String content;

    private int likeCnt;


    /**
     * Root Opin
     * @param content
     * @param vote
     */
    public static Opin of(String content, Vote vote) {
        return Opin.builder()
            .content(content)
            .vote(vote)
            .build();
    }

    /**
     * Reply Opin
     * @param content
     * @param vote
     * @param parentOpin
     */
    public static Opin of(String content, Vote vote, Opin parentOpin) {
        Opin childOpin = Opin.builder()
            .content(content)
            .vote(vote)
            .parent(parentOpin)
            .build();
        parentOpin.addChildOpin(childOpin);
        return childOpin;
    }

    private void addChildOpin(Opin childOpin) {
        this.children.add(childOpin);
        childOpin.setParentOpin(this);
    }

    private void setParentOpin(Opin parentOpin) {
        this.parent = parentOpin;
    }

    public void updateContent(String content) {
        this.content = content;
    }

}
