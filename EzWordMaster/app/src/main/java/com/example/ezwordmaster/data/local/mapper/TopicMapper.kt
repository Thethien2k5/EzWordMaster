package com.example.ezwordmaster.data.local.mapper

import com.example.ezwordmaster.data.local.entity.TopicEntity
import com.example.ezwordmaster.data.local.entity.WordEntity
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.model.Word

object TopicMapper {
    
    fun toDomain(topicEntity: TopicEntity, words: List<WordEntity>): Topic {
        return Topic(
            id = topicEntity.id,
            name = topicEntity.name,
            words = words.map { wordEntity ->
                Word(
                    word = wordEntity.word,
                    meaning = wordEntity.meaning,
                    example = wordEntity.example
                )
            }
        )
    }
    
    fun toEntity(topic: Topic): TopicEntity {
        return TopicEntity(
            id = topic.id ?: "",
            name = topic.name ?: ""
        )
    }
    
    fun wordToEntity(word: Word, topicId: String): WordEntity {
        return WordEntity(
            topicId = topicId,
            word = word.word,
            meaning = word.meaning,
            example = word.example
        )
    }
    
    fun wordToDomain(wordEntity: WordEntity): Word {
        return Word(
            word = wordEntity.word,
            meaning = wordEntity.meaning,
            example = wordEntity.example
        )
    }
}


