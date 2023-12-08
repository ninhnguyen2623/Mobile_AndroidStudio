package com.example.notesapp.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.notesapp.entities.Note;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class NoteDao_Impl implements NoteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Note> __insertionAdapterOfNote;

  private final EntityDeletionOrUpdateAdapter<Note> __deletionAdapterOfNote;

  public NoteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNote = new EntityInsertionAdapter<Note>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `notes` (`id`,`title`,`date_time`,`subtitle`,`note_text`,`image_path`,`color`,`web_link`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Note entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getDateTime() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDateTime());
        }
        if (entity.getSubtitle() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSubtitle());
        }
        if (entity.getNoteText() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNoteText());
        }
        if (entity.getImagePath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getImagePath());
        }
        if (entity.getColor() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getColor());
        }
        if (entity.getWebLink() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getWebLink());
        }
      }
    };
    this.__deletionAdapterOfNote = new EntityDeletionOrUpdateAdapter<Note>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `notes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Note entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public void insertNote(final Note note) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfNote.insert(note);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteNote(final Note note) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfNote.handle(note);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Note> getAllNotes() {
    final String _sql = "SELECT * FROM notes ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "date_time");
      final int _cursorIndexOfSubtitle = CursorUtil.getColumnIndexOrThrow(_cursor, "subtitle");
      final int _cursorIndexOfNoteText = CursorUtil.getColumnIndexOrThrow(_cursor, "note_text");
      final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
      final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
      final int _cursorIndexOfWebLink = CursorUtil.getColumnIndexOrThrow(_cursor, "web_link");
      final List<Note> _result = new ArrayList<Note>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Note _item;
        _item = new Note();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        _item.setTitle(_tmpTitle);
        final String _tmpDateTime;
        if (_cursor.isNull(_cursorIndexOfDateTime)) {
          _tmpDateTime = null;
        } else {
          _tmpDateTime = _cursor.getString(_cursorIndexOfDateTime);
        }
        _item.setDateTime(_tmpDateTime);
        final String _tmpSubtitle;
        if (_cursor.isNull(_cursorIndexOfSubtitle)) {
          _tmpSubtitle = null;
        } else {
          _tmpSubtitle = _cursor.getString(_cursorIndexOfSubtitle);
        }
        _item.setSubtitle(_tmpSubtitle);
        final String _tmpNoteText;
        if (_cursor.isNull(_cursorIndexOfNoteText)) {
          _tmpNoteText = null;
        } else {
          _tmpNoteText = _cursor.getString(_cursorIndexOfNoteText);
        }
        _item.setNoteText(_tmpNoteText);
        final String _tmpImagePath;
        if (_cursor.isNull(_cursorIndexOfImagePath)) {
          _tmpImagePath = null;
        } else {
          _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
        }
        _item.setImagePath(_tmpImagePath);
        final String _tmpColor;
        if (_cursor.isNull(_cursorIndexOfColor)) {
          _tmpColor = null;
        } else {
          _tmpColor = _cursor.getString(_cursorIndexOfColor);
        }
        _item.setColor(_tmpColor);
        final String _tmpWebLink;
        if (_cursor.isNull(_cursorIndexOfWebLink)) {
          _tmpWebLink = null;
        } else {
          _tmpWebLink = _cursor.getString(_cursorIndexOfWebLink);
        }
        _item.setWebLink(_tmpWebLink);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
