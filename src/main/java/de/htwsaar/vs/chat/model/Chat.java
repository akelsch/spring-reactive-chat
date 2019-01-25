package de.htwsaar.vs.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Chat Model (MongoDB Document)
 *
 * @author Niklas Reinhard
 */
@NoArgsConstructor
@Document
public class Chat {

    public String getId() {
        return this.id;
    }

    public Boolean getIsGroup() {
        return this.isGroup;
    }

    public String getName() {
        return this.name;
    }

    public List<Member> getMembers() {
        return this.members;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Chat)) return false;
        final Chat other = (Chat) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$isGroup = this.getIsGroup();
        final Object other$isGroup = other.getIsGroup();
        if (this$isGroup == null ? other$isGroup != null : !this$isGroup.equals(other$isGroup)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$members = this.getMembers();
        final Object other$members = other.getMembers();
        if (this$members == null ? other$members != null : !this$members.equals(other$members)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Chat;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $isGroup = this.getIsGroup();
        result = result * PRIME + ($isGroup == null ? 43 : $isGroup.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $members = this.getMembers();
        result = result * PRIME + ($members == null ? 43 : $members.hashCode());
        return result;
    }

    public String toString() {
        return "Chat(id=" + this.getId() + ", isGroup=" + this.getIsGroup() + ", name=" + this.getName() + ", members=" + this.getMembers() + ")";
    }

    @Data
    @NoArgsConstructor
    @Document
    static private class Member{
        @DBRef
        private User user;
        private Boolean isAdmin;
    }

    @Id
    private String id;
    private Boolean isGroup;
    private String name;
    private List<Member> members;
}
